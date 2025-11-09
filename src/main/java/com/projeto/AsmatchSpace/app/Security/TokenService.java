package com.projeto.AsmatchSpace.app.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret ;

    public String gerarToken(Usuario usuario){
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return  JWT.create()
                    .withIssuer("API AsmatchSpace")//que esta gerando a api(geralmente a dona da aplicacao)
                    .withSubject(usuario.getUsername())
                    .withClaim("id", usuario.getId())
                    .withClaim("role", usuario.getRole().name())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token jwt",exception);
            // Invalid Signing configuration / Couldn't convert Claims.
        }
    }
    public String getSubject(String tokenJWT){
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return  JWT.require(algoritmo)
                    .withIssuer("API AsmatchSpace")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirdo!"+tokenJWT);
        }

    }

    public Long getUserId(String tokenJWT){
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var decoded = JWT.require(algoritmo)
                    .withIssuer("API AsmatchSpace")
                    .build()
                    .verify(tokenJWT);

            return decoded.getClaim("id").asLong();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirdo!" + tokenJWT);
        }
    }



    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
