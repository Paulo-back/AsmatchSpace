package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;

import java.time.LocalDate;

// ─────────────────────────────────────────────────────────────
//  DadosDetalhamentoAdmin
//  Usado exclusivamente nos endpoints de gerenciamento (ADMIN).
//  Inclui role, ativo e todos os campos clínicos/endereço.
// ─────────────────────────────────────────────────────────────
public record DadosDetalhamentoAdmin(

        Long   id,
        String nome,
        String email,
        String telefone,
        String cpf,
        LocalDate dataNascimento,
        String sexo,

        // acesso
        String  role,
        Boolean ativo,

        // saúde
        String problema_respiratorio,
        String medicamentos,
        String alergias,
        String contatoEmergencia,

        // endereço
        Endereco endereco

) {
    public DadosDetalhamentoAdmin(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getUsuario().getLogin(), // email real de autenticação
                cliente.getTelefone(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getSexo(),

                // role vem do Usuario vinculado
                cliente.getUsuario()
                        .getAuthorities()
                        .stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse("USER"),

                cliente.getAtivo(),

                cliente.getProblema_respiratorio(),
                cliente.getMedicamentos(),
                cliente.getAlergias(),
                cliente.getContatoEmergencia(),
                cliente.getEndereco()
        );
    }
}