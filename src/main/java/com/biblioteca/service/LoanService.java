package com.biblioteca.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.User;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * Cria um novo empréstimo de livro para um utilizador.
     * Verifica se o livro está em stock e se já não está emprestado ao mesmo utilizador.
     */
    public Loan createLoan(Long userId, Long bookId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        if (book.getStock() <= 0) {
            throw new IllegalStateException("Livro fora de estoque");
        }

        // Verifica se o mesmo utilizador já tem este livro emprestado e não devolvido
        boolean alreadyBorrowed = loanRepo.existsByUserIdAndBookIdAndReturnDateIsNull(userId, bookId);
        if (alreadyBorrowed) {
            throw new IllegalStateException("Este livro já está emprestado a este utilizador.");
        }

        // Atualiza o stock do livro
        book.setStock(book.getStock() - 1);
        bookRepo.save(book);

        // Cria e salva o empréstimo
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(null);

        return loanRepo.save(loan);
    }

    /**
     * Realiza a devolução de um livro emprestado.
     * Atualiza o stock do livro e define a data de devolução.
     */
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado"));

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }

        // Atualiza o stock do livro devolvido
        Book book = loan.getBook();
        book.setStock(book.getStock() + 1);
        bookRepo.save(book);

        // Registra a data de devolução
        loan.setReturnDate(LocalDate.now());

        return loanRepo.save(loan);
    }
}
