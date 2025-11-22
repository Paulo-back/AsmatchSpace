package com.projeto.AsmatchSpace.app.Domain.Diario;

public record DadosListagemDiario(
        Long id,
        String data,
        String horario,
        String intensidade
) {
    public DadosListagemDiario(DiarioSintoma diario) {
        this(
                diario.getId(),
                diario.getData(),
                diario.getHorario(),
                diario.getIntensidade()
        );
    }
}
