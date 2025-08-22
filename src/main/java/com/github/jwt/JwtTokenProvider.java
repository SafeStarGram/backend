package com.github.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties props;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(props.getSecret());
    }

    private JWTVerifier verifier() {
        return JWT.require(algorithm())
                .withIssuer(props.getIssuer())
                .build();
    }

    public String generateAccessToken(Long userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getAccessTtl());
        return JWT.create()
                .withIssuer(props.getIssuer())
                .withSubject(String.valueOf(userId))
                .withClaim("role", role)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(algorithm());
    }

    public String generateRefreshToken(Long userId, String role, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getRefreshTtl());  // 예: 14d
        return JWT.create()
                .withIssuer(props.getIssuer())
                .withSubject(String.valueOf(userId))
                .withClaim("role", role)
                .withJWTId(jti)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(algorithm());
    }

    public boolean validate(String token) {
        try { verifier().verify(token); return true; }
        catch (Exception e) { return false; }
    }

    public DecodedJWT decode(String token) {
        return verifier().verify(token);
    }

    public Long getUserId(String token) {
        return Long.valueOf(decode(token).getSubject());
    }

    public String getRole(String token) {
        return decode(token).getClaim("role").asString();
    }

    public String getJti(String token) {
        return decode(token).getId();
    }

    public String newJti() {
        return UUID.randomUUID().toString();
    }

}
