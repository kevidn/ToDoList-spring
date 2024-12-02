package com.project.todolist.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.todolist.repository.Repository;
import com.project.todolist.models.Task;

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
            taskMap.put("title", task.getTitle());
            taskMap.put("description", task.getDescription());
            taskMap.put("createdAt", task.getCreatedAt().format(formatter));
            taskMap.put("updatedAt", task.getUpdatedAt().format(formatter));
            return taskMap;
        }).toList();
    }

    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id);
    }

    public Task createAllTask(Task task) {
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTaskOptional = repository.findById(id);

        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();

            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setUpdatedAt(LocalDateTime.now());

            return repository.save(existingTask);
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }

    public void deleteTask(Long id) {
        Task task = repository.findById(id).orElse(null);
        if (task == null) {
            throw new RuntimeException("Task with id : '" + id + "' not found.");
        }

        repository.delete(task);
    }

}
