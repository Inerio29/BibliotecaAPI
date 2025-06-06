package com.biblioteca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.model.User;
import com.biblioteca.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado"));
    }

    public User createUser(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Já existe um usuário com este email");
        }
        return userRepo.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado"));
        
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        
        return userRepo.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new EntityNotFoundException("Usuário com ID " + id + " não encontrado");
        }
        userRepo.deleteById(id);
    }
}
