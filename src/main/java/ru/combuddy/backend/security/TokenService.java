package ru.combuddy.backend.security;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.accessToken.expiresInHours}")
    private Long accessTokenExpiresInHours;
    @Value("${jwt.refreshToken.expiresInHours}")
    private Long refreshTokenExpiresInHours;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(username, authorities, accessTokenExpiresInHours);
    }

    public String generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(username, authorities, refreshTokenExpiresInHours);
    }

    private String generateToken(String username, Collection<? extends GrantedAuthority> authorities, long expiresInHours) {
        var now = Instant.now();
        var expiresAt = now.plus(expiresInHours, ChronoUnit.HOURS);
        var scopes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("scope", scopes)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
