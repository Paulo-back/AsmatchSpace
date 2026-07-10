package com.projeto.AsmatchSpace.app.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(destinatario);
        msg.setSubject("Asthma Space - Código de recuperação de senha");
        msg.setText("Olá!\n\nSeu código de recuperação é: " + codigo
                + "\n\nEle expira em 15 minutos."
                + "\nSe você não solicitou esta recuperação, ignore este email.");
        mailSender.send(msg);
    }
}
