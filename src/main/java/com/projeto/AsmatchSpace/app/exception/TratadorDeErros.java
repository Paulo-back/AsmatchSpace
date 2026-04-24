package com.projeto.AsmatchSpace.app.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrosPadroes> tratarErroValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensagem = ex.getFieldErrors()
                .stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrosPadroes erro = new ErrosPadroes(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErrosPadroes> tratarValidacaoCustom(
            ValidacaoException ex,
            HttpServletRequest request) {

        ErrosPadroes erro = new ErrosPadroes(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(erro);
    }
}
