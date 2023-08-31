package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.exceptions.user.authentication.InvalidAuthenticationException;

@RestController
@RequestMapping("/api/v1/user/auth")
@AllArgsConstructor
public class AuthController {

    private final UserBaseAuthService userBaseAuthService;

    @PostMapping("/register/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@PathVariable String username, @RequestParam String password) {
        return userBaseAuthService.registerUser(username, password);
    }

    @PostMapping("/login/{username}")
    public LoginResponse login(@PathVariable String username, @RequestParam String password) {
        return userBaseAuthService.login(username, password);
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("true")
    public LoginResponse refreshToken(Authentication authentication) {
        JwtAuthenticationToken jwtRefreshToken;
        try {
            jwtRefreshToken = (JwtAuthenticationToken) authentication;
        } catch (ClassCastException e) {
            throw new InvalidAuthenticationException("Need refresh token in authentication");
        }
        var username = getUsername(jwtRefreshToken);
        return userBaseAuthService.refreshToken(jwtRefreshToken, username);
    }

    @PostMapping("/logout")
    @PreAuthorize("true")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication) {
        var username = getUsername(authentication);
        userBaseAuthService.logout(username);
    }


    public static String getUsername(Authentication authentication) {
        return authentication.getName();
    }

    public static String getUsername(JwtAuthenticationToken jwtAuthenticationToken) {
        return jwtAuthenticationToken.getName();
    }
}