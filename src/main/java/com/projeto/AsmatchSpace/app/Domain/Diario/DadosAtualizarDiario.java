package com.projeto.AsmatchSpace.app.Domain.Diario;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosAtualizarDiario(
        LocalDate data,
        LocalTime horario,
        String intensidade,
        String descricao
) {}

