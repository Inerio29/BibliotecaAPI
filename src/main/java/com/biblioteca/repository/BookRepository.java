package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.biblioteca.model.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Buscar livros contendo parte do título (ignorando maiúsculas/minúsculas)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Buscar livros contendo parte do nome do autor (ignorando maiúsculas/minúsculas)
    List<Book> findByAuthorContainingIgnoreCase(String author);
}
