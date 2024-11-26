package com.project.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.todolist.models.Task;

public interface Repository extends JpaRepository<Task, Integer> {

}
