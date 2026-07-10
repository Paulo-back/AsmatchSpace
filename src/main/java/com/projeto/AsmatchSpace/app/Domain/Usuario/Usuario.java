package com.projeto.AsmatchSpace.app.Domain.Usuario;


import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String senha;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String codigoRecuperacao;

    private java.time.LocalDateTime codigoExpiracao;

    private java.time.LocalDateTime senhaAlteradaEm;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Cliente cliente;

    public Usuario(String dadosLogin, String dadosSenha) {
        this.login = dadosLogin;
        this.senha = dadosSenha;
        this.role = Role.ROLE_USER;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }//classe que controle perfil, tipo perfies de administrador, etc

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void definirCodigoRecuperacao(String codigo, java.time.LocalDateTime expiracao) {
        this.codigoRecuperacao = codigo;
        this.codigoExpiracao = expiracao;
    }

    public void limparCodigoRecuperacao() {
        this.codigoRecuperacao = null;
        this.codigoExpiracao = null;
    }

    public boolean codigoValido(String codigo) {
        return this.codigoRecuperacao != null
                && this.codigoRecuperacao.equals(codigo)
                && this.codigoExpiracao != null
                && this.codigoExpiracao.isAfter(java.time.LocalDateTime.now(
                java.time.ZoneId.of("America/Sao_Paulo")));
    }

    public void registrarTrocaDeSenha() {
        this.senhaAlteradaEm = java.time.LocalDateTime.now(
                java.time.ZoneId.of("America/Sao_Paulo"));
    }
}

