package com.projeto.AsmatchSpace.app.Security;


import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.Domain.Usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                String login = tokenService.getSubject(tokenJWT);

                Usuario usuario = repository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

                // Invalida tokens emitidos antes da última troca de senha
                if (tokenEmitidoAntesDaTrocaDeSenha(tokenJWT, usuario)) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Token inválido, expirado ou usuário não existe mais
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var auth = request.getHeader("Authorization");
        if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
            return auth.substring(7).trim();
        }
        return null;
    }

    private boolean tokenEmitidoAntesDaTrocaDeSenha(String tokenJWT, Usuario usuario) {
        var senhaAlteradaEm = usuario.getSenhaAlteradaEm();
        if (senhaAlteradaEm == null) return false; // nunca trocou desde a feature

        var iat = tokenService.getIssuedAt(tokenJWT);
        if (iat == null) return false; // token antigo, sem iat — não invalida

        // Converte a troca de senha (gravada em horário de SP) para Instant
        var trocaInstant = senhaAlteradaEm
                .atZone(java.time.ZoneId.of("America/Sao_Paulo"))
                .toInstant();

        // Margem de 5s: o iat trunca para segundos, e login logo após a troca
        // pode gerar token no mesmo segundo — sem margem, seria falso positivo
        return iat.isBefore(trocaInstant.minusSeconds(5));
    }

}

