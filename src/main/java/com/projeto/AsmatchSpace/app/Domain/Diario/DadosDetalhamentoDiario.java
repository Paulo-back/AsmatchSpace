package com.projeto.AsmatchSpace.app.Domain.Diario;

public record DadosDetalhamentoDiario(
        Long id,
        String data,
        String horario,
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