package ru.combuddy.backend.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.function.Predicate;

public class JwtLockableAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final Predicate<String> isUserLocked;

    public JwtLockableAuthenticationProvider(JwtDecoder jwtDecoder, Predicate<String> isUserLocked) {
        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        this.isUserLocked = isUserLocked;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = jwtAuthenticationProvider.authenticate(authentication);
        var username = token.getName();
        if (isUserLocked.test(username)) {
            throw new LockedException(
                    MessageFormat.format("User with username {0} is locked",
                            username));
        }
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return jwtAuthenticationProvider.supports(authentication);
    }

    public void setJwtAuthenticationConverter(
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) {
        jwtAuthenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
    }
}
