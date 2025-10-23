package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import jakarta.validation.Valid;

public record DadosAtualizacaoCliente(
    Long id,
    String medicamentos,
    String telefone,
    @Valid
    DadosEndereco endereco

            ){
}



//public record DadosAtualizacaoPaciente(
//        Long id,
//        String nome,
//        String telefone,
//        @Valid DadosEndereco endereco
//) {
//}