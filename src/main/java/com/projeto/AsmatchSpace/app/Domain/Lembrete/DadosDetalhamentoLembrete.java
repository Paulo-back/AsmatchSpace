package com.projeto.AsmatchSpace.app.Domain.Lembrete;

public record DadosDetalhamentoLembrete(
        Long id,
        String titulo,
        String horario,
        String data,
        Boolean concluido
) {
    public DadosDetalhamentoLembrete(Lembretes lembrete) {
        this(
                lembrete.getId(),
                lembrete.getTitulo(),
                lembrete.getHorario(),
                lembrete.getData(),
                lembrete.isConcluido()
        );
    }
}

