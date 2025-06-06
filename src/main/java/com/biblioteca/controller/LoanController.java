package com.biblioteca.controller;

import java.util.List;

import com.biblioteca.model.Loan;
import com.biblioteca.service.LoanService;
import com.biblioteca.repository.LoanRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@Tag(name = "Empréstimos", description = "Operações relacionadas ao sistema de empréstimos de livros")
public class LoanController {

    @Autowired
    private LoanRepository repo;

    @Autowired
    private LoanService service;

    @Operation(
        summary = "Listar todos os empréstimos",
        description = "Retorna uma lista com todos os empréstimos registrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de empréstimos retornada com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Loan.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum empréstimo encontrado")
    })
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = repo.findAll();
        if (loans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(loans);
    }

    @Operation(
        summary = "Criar novo empréstimo",
        description = "Cria um novo empréstimo de livro para um usuário específico. Verifica disponibilidade do livro e se o usuário já possui o mesmo livro emprestado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Empréstimo criado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Loan.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou livro indisponível"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createLoan(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @RequestParam Long userId, 
            @Parameter(description = "ID do livro", required = true, example = "1")
            @RequestParam Long bookId) {
        try {
            Loan newLoan = service.createLoan(userId, bookId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newLoan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar empréstimo.");
        }
    }

    @Operation(
        summary = "Devolver livro emprestado",
        description = "Registra a devolução de um livro emprestado e atualiza o estoque"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro devolvido com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Loan.class))),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/{loanId}/return")
    public ResponseEntity<?> returnLoan(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long loanId) {
        try {
            Loan returnedLoan = service.returnLoan(loanId);
            return ResponseEntity.ok(returnedLoan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao devolver livro.");
        }
    }

    @Operation(
        summary = "Buscar empréstimo por ID",
        description = "Retorna os detalhes de um empréstimo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo encontrado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Loan.class))),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(loan -> ResponseEntity.ok(loan))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Empréstimo não encontrado."));
    }

    @Operation(
        summary = "Eliminar empréstimo",
        description = "Remove um registro de empréstimo do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo eliminado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLoan(
            @Parameter(description = "ID do empréstimo", required = true, example = "1")
            @PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empréstimo não encontrado.");
        }
        repo.deleteById(id);
        return ResponseEntity.ok("Empréstimo eliminado com sucesso.");
    }
}
