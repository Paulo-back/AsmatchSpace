package com.projeto.AsmatchSpace.app.exception;

public class ContaDesativadaException extends RuntimeException {
    public ContaDesativadaException() {
        super("Conta desativada. Entre em contato com o suporte.");
    }
}