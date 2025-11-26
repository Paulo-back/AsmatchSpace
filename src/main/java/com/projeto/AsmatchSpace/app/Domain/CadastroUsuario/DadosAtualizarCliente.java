package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;

public record DadosAtualizarCliente(
//    Long id,
    String medicamentos,
    String telefone,
    String sexo,
    String problema_respiratorio,
    String alergias,
    String contatoEmergencia,
    @Valid
    DadosEndereco endereco
            ){
}
