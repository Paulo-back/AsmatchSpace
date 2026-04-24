package com.projeto.AsmatchSpace.app.Domain.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class VerificarIdentidadeRequest {

    @NotBlank
    private String email;

    @NotNull
    private LocalDate dataNascimento;

    private String cpf; // opcional

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public void setDataNascimento(LocalDate d) { this.dataNascimento = d; }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
