package com.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuário pelo email
    Optional<User> findByEmail(String email);

    // Verificar se existe usuário com email
    boolean existsByEmail(String email);
}
