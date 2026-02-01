package io.github.guilherme_eira.wallet_service.adapter.outbound.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.guilherme_eira.wallet_service.application.exception.AuthTokenGenerationException;
import io.github.guilherme_eira.wallet_service.application.exception.AuthTokenVerificationException;
import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TokenProviderAdapter implements TokenProvider {

    @Value("${api.security.jwt-secret}")
    private String secret;
    private final String ISSUER = "Wallet Service API";

    @Override
    public String generateAccessToken(User user, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withClaim("role", role)
                    .withExpiresAt(genExpirationDate(15, ChronoUnit.MINUTES))
                    .sign(algorithm);
        } catch (JWTCreationException ex){
            throw new AuthTokenGenerationException("Ocorreu um erro ao gerar o token de acesso", ex);
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate(12, ChronoUnit.HOURS))
                    .sign(algorithm);
        } catch (JWTCreationException ex){
            throw new AuthTokenGenerationException("Ocorreu um erro ao gerar o refresh token", ex);
        }
    }

    @Override
    public String getSubject(String token) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token);
            if (decodedJWT.getSubject().isEmpty()) {
                return null;
            }
            return decodedJWT.getSubject();
        } catch (JWTVerificationException ex){
            throw new AuthTokenVerificationException("Token expirado ou mal formatado", ex);
        }
    }

    @Override
    public String getRole(String token) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token);
            if (decodedJWT.getClaim("role").isNull()) {
                return null;
            }
            return decodedJWT.getClaim("role").asString();
        } catch (JWTVerificationException ex){
            throw new AuthTokenVerificationException("Token expirado ou mal formatado", ex);
        }
    }

    private DecodedJWT getDecodedJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    private Instant genExpirationDate(Integer amount,ChronoUnit unit){
        return Instant.now().plus(amount, unit);
    }

}
