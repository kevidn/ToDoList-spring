package com.project.todolist.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.todolist.models.Task;
import com.project.todolist.models.User;
import com.project.todolist.repository.Repository;

@Service
public class TaskService {

    private final Repository repository;
    private final UserService userService;

    public TaskService(Repository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<Map<String, Object>> getAllTask() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User currentUser = getCurrentUser();
        List<Task> tasks = repository.findByUser(currentUser);

        return tasks.stream().map(task -> {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", task.getId());
            taskMap.put("title", task.getTitle() != null ? task.getTitle() : "-");
            taskMap.put("description", task.getDescription() != null ? task.getDescription() : "-");
            taskMap.put("deadline",
                    task.getDeadline() != null ? task.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            : "-");
            taskMap.put("createdAt", task.getCreatedAt() != null ? task.getCreatedAt().format(formatter) : "-");
            taskMap.put("updatedAt", task.getUpdatedAt() != null ? task.getUpdatedAt().format(formatter) : "-");
            taskMap.put("status", task.getStatus() != null ? task.getStatus().name() : "-");

            LocalDate deadline = task.getDeadline();
            LocalDate today = LocalDate.now();
            long remainingDays = ChronoUnit.DAYS.between(today, deadline);

            String remainingDaysText;
            String remainingDaysClass;
            if (remainingDays < 0) {
                remainingDaysText = "Passed";
                remainingDaysClass = "text-red-500";
            } else if (remainingDays == 0) {
                remainingDaysText = "Today";
                remainingDaysClass = "text-yellow-500";
            } else {
                remainingDaysText = remainingDays + " days left";
                remainingDaysClass = "text-green-500";
            }

            taskMap.put("remainingDays", remainingDaysText);
            taskMap.put("remainingDaysClass", remainingDaysClass);
            return taskMap;
        }).toList();
    }

    public void save(Task task) {
        task.setUser(getCurrentUser());
        repository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id).filter(task -> task.getUser().equals(getCurrentUser()));
    }

    public Task createAllTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setUser(getCurrentUser());
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTaskOptional = repository.findById(id)
                .filter(task -> task.getUser().equals(getCurrentUser()));

        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();

            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setUpdatedAt(LocalDateTime.now());
            existingTask.setStatus(updatedTask.getStatus());
            existingTask.setDeadline(updatedTask.getDeadline());

            return repository.save(existingTask);
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }

    public void deleteTask(Long id) {
        Optional<Task> task = repository.findById(id).filter(t -> t.getUser().equals(getCurrentUser()));

        if (task.isPresent()) {
            repository.delete(task.get());
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userService.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userService.findByEmail(email);
    }
}