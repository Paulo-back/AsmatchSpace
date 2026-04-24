package com.projeto.AsmatchSpace.app.Domain.Diario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosCadastroDiario(
        @NotNull LocalDate data,
        @NotNull LocalTime horario,
        @NotBlank String intensidade,
        @NotBlank String descricao
) {}
