package com.project.todolist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import com.project.todolist.models.Task;

public interface Repository extends JpaRepository<Task, Integer> {

    Optional<Task> findById(Long id);

    Task deleteById(Long id);

    List<Task> findAll(Sort sort);

}
