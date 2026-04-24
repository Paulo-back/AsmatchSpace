package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosCadastroLembrete(
        @NotBlank String titulo,
        @NotNull LocalTime horario,
        @NotNull LocalDate data
) {}
