package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.Embedded;

public record DadosListagemClientes(
        Long id, String nome, String email, String telefone,String problema_respiratorio,
        String medicamentos, String alergias, String contatoEmergencia
) {
    public DadosListagemClientes(Cliente cliente){
        this(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefone(), cliente.getProblema_respiratorio(),
                cliente.getMedicamentos(), cliente.getAlergias(), cliente.getContatoEmergencia());
    }

}
