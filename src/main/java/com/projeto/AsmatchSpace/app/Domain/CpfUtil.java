package com.projeto.AsmatchSpace.app.Domain;

public class CpfUtil {

    public static String normalizar(String cpf) {
        if (cpf == null) return null;

        String apenasDigitos = cpf.replaceAll("[^\\d]", "");

        if (apenasDigitos.length() != 11) return cpf;

        return apenasDigitos.substring(0, 3) + "." +
                apenasDigitos.substring(3, 6) + "." +
                apenasDigitos.substring(6, 9) + "-" +
                apenasDigitos.substring(9, 11);
    }

    public static boolean isValido(String cpf) {
        if (cpf == null) return false;
        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}");
    }
}
