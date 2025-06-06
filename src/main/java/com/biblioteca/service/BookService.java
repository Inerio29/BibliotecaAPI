package com.biblioteca.service;


import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Criar novo livro
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    // Listar todos os livros
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Buscar livro por ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com ID: " + id));
    }

    // Atualizar livro
    public Book updateBook(Long id, Book updatedBook) {
        Book existing = getBookById(id);
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setStock(updatedBook.getStock());
        return bookRepository.save(existing);
    }

    // Deletar livro
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Livro não encontrado com ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Buscar por título
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Buscar por autor
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // Verificar disponibilidade
    public boolean isBookAvailable(Long id) {
        Book book = getBookById(id);
        return book.getStock() > 0;
    }
}
