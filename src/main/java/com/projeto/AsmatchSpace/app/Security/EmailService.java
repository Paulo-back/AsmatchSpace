package com.projeto.AsmatchSpace.app.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    public EmailService(@Value("${brevo.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Async
    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", "Asthma Space",
                        "email", "asthmaspace.noreply@gmail.com"
                ),
                "to", List.of(Map.of("email", destinatario)),
                "subject", "Asthma Space - Código de recuperação de senha",
                "textContent", "Olá!\n\nSeu código de recuperação é: " + codigo
                        + "\n\nEle expira em 15 minutos."
                        + "\nSe você não solicitou esta recuperação, ignore este email."
        );

        try {
            restClient.post()
                    .uri("/smtp/email")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // Não propaga: falha de email não deve derrubar o fluxo (e é @Async)
            System.err.println("Falha ao enviar email de recuperação: " + e.getMessage());
        }
    }
}