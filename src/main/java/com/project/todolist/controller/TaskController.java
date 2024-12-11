package com.project.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.todolist.models.Task;
import com.project.todolist.models.Task.TaskStatus;
import com.project.todolist.service.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Method untuk menampilkan halaman
    @GetMapping
    public String getAllTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTask());
        return "task/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task/create";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "task/edit";
    }

    // Method untuk melakukan request
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task) {
        taskService.createAllTask(task);
        return "redirect:/task";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        taskService.updateTask(id, task);
        return "redirect:/task";
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/task";
    }
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long taskId, @RequestParam String status) {
    Task task = taskService.getTaskById(taskId)
        .orElseThrow(() -> new RuntimeException("Task not found"));

    task.setStatus(TaskStatus.valueOf(status));  // Mengupdate status task
    taskService.save(task);  // Menyimpan task yang sudah diupdate
    return  "redirect:/task";
    }
}
