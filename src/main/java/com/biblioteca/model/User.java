package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Schema(description = "Entidade representando um usuário da biblioteca")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter até 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva", required = true, maxLength = 100)
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Column(unique = true)
    @Schema(description = "Email único do usuário", example = "joao@email.com", required = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Schema(description = "Lista de empréstimos do usuário", accessMode = Schema.AccessMode.READ_ONLY)
    private Set<Loan> loans = new HashSet<>();

    // Construtores
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Set<Loan> getLoans() { return loans; }
    public void setLoans(Set<Loan> loans) { this.loans = loans; }

    // Métodos auxiliares
    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.setUser(this);
    }

    public void removeLoan(Loan loan) {
        loans.remove(loan);
        loan.setUser(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
