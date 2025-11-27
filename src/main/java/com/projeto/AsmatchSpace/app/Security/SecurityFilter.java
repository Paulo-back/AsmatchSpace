package com.projeto.AsmatchSpace.app.Security;


import com.projeto.AsmatchSpace.app.Domain.Usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("CHAMANDO FILTER!!!");
        var tokenJWT = recuperarToken(request);
//        System.out.println("TOKEN"+tokenJWT);
        if (tokenJWT != null){
            var subject = tokenService.getSubject(tokenJWT);//recupera o token do cabeçalho
            var usuario = repository.findByLogin(subject);
            var authentication = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);//Força a autenticacao

            System.out.println("LOGADO NA REQUISIÇÃO");
        }

//        System.out.println(subject);

        filterChain.doFilter(request,response);

    }

    private String recuperarToken(HttpServletRequest request) {
        var auth = request.getHeader("Authorization");
        if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
            return auth.substring(7).trim(); // remove “Bearer “ (7 chars)
        }
        return null;
    }

}

