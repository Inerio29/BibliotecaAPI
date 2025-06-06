package com.biblioteca.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.model.User;
import com.biblioteca.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Operações relacionadas ao gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
        summary = "Listar todos os usuários",
        description = "Retorna uma lista com todos os usuários cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna um usuário específico baseado no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
        summary = "Criar novo usuário",
        description = "Cria um novo usuário no sistema com nome e email únicos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "409", description = "Email já existe no sistema")
    })
    @PostMapping
    public ResponseEntity<?> createUser(
            @Parameter(description = "Dados do usuário a ser criado", required = true)
            @Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Novos dados do usuário", required = true)
            @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }

    @Operation(
        summary = "Remover usuário",
        description = "Remove um usuário do sistema baseado no ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
