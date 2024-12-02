package com.project.todolist.controller;

import com.project.todolist.service.TaskService;
import com.project.todolist.models.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "task/index"; // Returns index.html
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
        // Logika penghapusan task
        taskService.deleteTask(id);
        return "redirect:/task/list"; // Redirect ke halaman daftar task setelah dihapus
    }
}
