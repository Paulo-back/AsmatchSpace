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
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();



        if (path.startsWith("/actuator") ||
                path.equals("/login") ||
                path.equals("/clientes/cadastro") ||
                path.startsWith("/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"UP\"}");
            return;
        }
        System.out.println("==> URI: [" + path + "]");
        System.out.println("==> ContextPath: [" + contextPath + "]");
        System.out.println("==> ServletPath: [" + servletPath + "]");
        System.out.println("==> starts with /actuator: " + path.startsWith("/actuator"));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Não autorizado\"}");
    }
}
