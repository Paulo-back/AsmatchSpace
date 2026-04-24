package com.projeto.AsmatchSpace.app.Domain.Usuario;

public class VerificarIdentidadeResponse {

    private String tokenRedefinicao;

    public VerificarIdentidadeResponse(String tokenRedefinicao) {
        this.tokenRedefinicao = tokenRedefinicao;
    }

    public String getTokenRedefinicao() { return tokenRedefinicao; }
    public void setTokenRedefinicao(String tokenRedefinicao) { this.tokenRedefinicao = tokenRedefinicao; }
}