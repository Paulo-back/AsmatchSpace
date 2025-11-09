package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;

public record DadosDetalhamentoCliente(
        String nome,
        String email,
        String telefone,
        String cpf,
        Integer idade,
        String sexo,
        Endereco endereco,
        String problema_respiratorio,
        String medicamentos,
        String alergias,
        String contatoEmergencia
//        String senha
         ){
   public DadosDetalhamentoCliente(Cliente cliente){
      this(
              cliente.getNome(),
              cliente.getEmail(),
              cliente.getTelefone(),
              cliente.getCpf(),
              cliente.getIdade(),
              cliente.getSexo(),
              cliente.getEndereco(),
              cliente.getProblema_respiratorio(),
              cliente.getMedicamentos(),
              cliente.getAlergias(),
              cliente.getContatoEmergencia()
//              cliente.getSenha()
      );

   }


}
