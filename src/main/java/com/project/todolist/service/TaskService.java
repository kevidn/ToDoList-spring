package com.project.todolist.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.todolist.models.Task;
import com.project.todolist.repository.Repository;

@Service
public class TaskService {

    private final Repository repository;

    public TaskService(Repository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> getAllTask() {
        // Untuk mem-format tanggal
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Mengambil semua task dari repository
        List<Task> tasks = repository.findAll();

        // Mengubah setiap task menjadi Map dengan tanggal terformat
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
                remainingDaysClass = "text-red-500"; // Red for passed deadlines
            } else if (remainingDays == 0) {
                remainingDaysText = "Today";
                remainingDaysClass = "text-yellow-500"; // Yellow for today's deadline
            } else {
                remainingDaysText = remainingDays + " days left";
                remainingDaysClass = "text-green-500"; // Green for more than 1 day left
            }

            taskMap.put("remainingDays", remainingDaysText);
            taskMap.put("remainingDaysClass", remainingDaysClass);
            return taskMap;
        }).toList();
    }

    public void save(Task task) {
        repository.save(task); // Pastikan taskRepository digunakan untuk menyimpan task
    }

    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id);
    }

    public Task createAllTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTaskOptional = repository.findById(id);

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
        Optional<Task> task = repository.findById(id);

        if (task.isPresent()) {
            repository.delete(task.get());
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }

}
