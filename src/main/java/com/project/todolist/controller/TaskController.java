package com.project.todolist.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.todolist.models.Task;
import com.project.todolist.repository.Repository;

@Controller
@RequestMapping("/task")
public class TaskController {

    private Repository repo;

    @Autowired
    public TaskController(Repository repo) {
        this.repo = repo;
    }

    @GetMapping({ "", "/" })
    public String showTask(Model model) {
        List<Task> tasks = repo.findAll();

        // Membuat format tanggal
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format createdAt dan updatedAt untuk setiap task dan tambahkan ke task itu
        // sendiri
        for (Task task : tasks) {
            String formattedCreatedAt = task.getCreatedAt().format(formatter);
            String formattedUpdatedAt = task.getUpdatedAt().format(formatter);

            // Mengirimkan hasil format tanggal ke masing-masing task
            task.setFormattedCreatedAt(formattedCreatedAt); // Asumsi Anda punya setter/getter untuk field ini
            task.setFormattedUpdatedAt(formattedUpdatedAt); // Asumsi Anda punya setter/getter untuk field ini
        }

        // Kirimkan daftar task ke view
        model.addAttribute("tasks", tasks);
        return "task/index"; // Kembali ke tampilan index
    }

    // Menampilkan halaman create task
    @GetMapping("/create")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task()); // Mengirim objek Task kosong ke form
        return "task/create"; // Halaman form create task
    }

    // Menyimpan task baru
    @PostMapping("/create")
    public String createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now()); // Menambahkan waktu saat task dibuat
        task.setUpdatedAt(LocalDateTime.now()); // Menambahkan waktu saat task dibuat (untuk keperluan update nanti)
        repo.save(task); // Menyimpan task ke database
        return "redirect:/task"; // Redirect ke halaman daftar task setelah berhasil menambahkan
    }

    // Menampilkan halaman edit task
    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = repo.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "task/edit"; // Halaman form edit task
    }

    // Menyimpan perubahan task
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, Task updatedTask) {
        Task task = repo.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setUpdatedAt(LocalDateTime.now()); // Jika menggunakan field updatedAt

        repo.save(task); // Menyimpan perubahan ke database
        return "redirect:/task"; // Redirect kembali ke halaman daftar task
    }

    @Transactional
    @PostMapping("/{id}")
    public String deleteTask(@PathVariable Long id, @RequestParam("_method") String method) {
        if ("DELETE".equalsIgnoreCase(method)) {
            repo.deleteById(id); // Menghapus task berdasarkan id
        }
        return "redirect:/task"; // Redirect ke halaman daftar task
    }
}
