package com.project.todolist.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.todolist.models.Task;
import com.project.todolist.repository.Repository;

@Controller
@RequestMapping("/task")
public class TaskController {
    
    private Repository repo;

    @GetMapping({"", "/"})
    public String showTask(Model model) {
        List<Task> task = repo.findAll();
        model.addAttribute("task", task);
        return "task/index";
    }
}
