package com.projeto.AsmatchSpace.app.exception;

import java.time.LocalDateTime;

public record ErrosPadroes(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path
) {}
