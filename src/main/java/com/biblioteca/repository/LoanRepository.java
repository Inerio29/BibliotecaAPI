package com.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Buscar empréstimos por ID do usuário
    List<Loan> findByUserId(Long userId);

    // Buscar empréstimos por ID do livro
    List<Loan> findByBookId(Long bookId);

    // Buscar todos os empréstimos não devolvidos
    List<Loan> findByReturnDateIsNull();

    // Buscar empréstimos de um determinado usuário ainda não devolvidos
    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);
    
    boolean existsByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);

}
