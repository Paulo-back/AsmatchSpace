package com.projeto.AsmatchSpace.app.Domain.Diario;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosDetalhamentoDiario(
        Long id,
        LocalDate data,
        LocalTime horario,
        String intensidade,
        String descricao
) {
    public DadosDetalhamentoDiario(DiarioSintoma diario) {
        this(
                diario.getId(),
                diario.getData(),
                diario.getHorario(),
                diario.getIntensidade(),
                diario.getDescricao()
        );
    }
}