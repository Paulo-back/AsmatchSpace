package com.projeto.AsmatchSpace.app.Domain;

public class CpfValidator {

    public static boolean isValidCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Elimina CPFs com todos os números iguais (ex: 11111111111)
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            int peso = 10;

            // Primeiro dígito verificador
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - 48) * peso--;
            }

            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;

            soma = 0;
            peso = 11;

            // Segundo dígito verificador
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - 48) * peso--;
            }

            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;

            // Valida com os dígitos do CPF informado
            return (primeiroDigito == (cpf.charAt(9) - 48)) &&
                    (segundoDigito == (cpf.charAt(10) - 48));

        } catch (Exception e) {
            return false;
        }
    }
}

