package com.projeto.AsmatchSpace.app.Domain.Usuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroUsuario(
        @NotBlank String login,
        @NotBlank String senha) {


}
