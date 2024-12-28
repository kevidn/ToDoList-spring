package com.project.todolist.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.todolist.models.User;
import com.project.todolist.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}