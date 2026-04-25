package com.projeto.AsmatchSpace.app.Security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String path = request.getRequestURI();

        // Rotas públicas não devem retornar 401
        if (path.startsWith("/actuator") ||
                path.equals("/login") ||
                path.equals("/clientes/cadastro") ||
                path.startsWith("/auth/recuperar-senha") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado");
    }
}
