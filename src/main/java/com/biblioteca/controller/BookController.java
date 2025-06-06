package com.biblioteca.controller;

import com.biblioteca.model.Book;
import com.biblioteca.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Livros", description = "Operações relacionadas ao gerenciamento de livros")
public class BookController {
    
    @Autowired
    private BookService bookService;

    @Operation(
        summary = "Criar novo livro",
        description = "Adiciona um novo livro ao acervo da biblioteca"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<Book> createBook(
            @Parameter(description = "Dados do livro a ser criado", required = true)
            @Valid @RequestBody Book book) {
        Book created = bookService.createBook(book);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(
        summary = "Listar todos os livros",
        description = "Retorna uma lista com todos os livros do acervo"
    )
    @ApiResponse(responseCode = "200", description = "Lista de livros retornada com sucesso")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(
        summary = "Buscar livro por ID",
        description = "Retorna um livro específico baseado no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Atualizar livro",
        description = "Atualiza os dados de um livro existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Novos dados do livro", required = true)
            @Valid @RequestBody Book book) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, book));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Deletar livro",
        description = "Remove um livro do acervo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Livro removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Buscar livros por título",
        description = "Retorna livros que contenham o título especificado (busca parcial)"
    )
    @ApiResponse(responseCode = "200", description = "Lista de livros encontrados")
    @GetMapping("/search/title")
    public List<Book> searchByTitle(
            @Parameter(description = "Título ou parte do título do livro", required = true, example = "1984")
            @RequestParam String title) {
        return bookService.searchByTitle(title);
    }

    @Operation(
        summary = "Buscar livros por autor",
        description = "Retorna livros que contenham o autor especificado (busca parcial)"
    )
    @ApiResponse(responseCode = "200", description = "Lista de livros encontrados")
    @GetMapping("/search/author")
    public List<Book> searchByAuthor(
            @Parameter(description = "Nome ou parte do nome do autor", required = true, example = "Orwell")
            @RequestParam String author) {
        return bookService.searchByAuthor(author);
    }

    @Operation(
        summary = "Verificar disponibilidade de um livro",
        description = "Verifica se um livro está disponível para empréstimo (estoque > 0)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status de disponibilidade retornado"),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @GetMapping("/{id}/available")
    public ResponseEntity<String> isBookAvailable(
            @Parameter(description = "ID do livro", required = true, example = "1")
            @PathVariable Long id) {
        try {
            boolean available = bookService.isBookAvailable(id);
            return ResponseEntity.ok(available ? "Disponível" : "Indisponível");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}