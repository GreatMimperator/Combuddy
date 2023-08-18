package ru.combuddy.backend.controllers.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.service.interfaces.UserRoleService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.security.repositories.WorkingRefreshTokenRepository;

import static ru.combuddy.backend.entities.user.UserAccount.getRoles;

@RestController
@RequestMapping("/api/user/auth")
@AllArgsConstructor
public class AuthController {

    private final UserAccountService userAccountService;
    private final UserRoleService userRoleService;
    private final UserBaseAuthService userBaseAuthService;
    private final WorkingRefreshTokenRepository workingRefreshTokenRepository;

    @PostMapping("/register/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public LoginResponse register(@PathVariable String username, @RequestParam String password) {
        UserAccount userAccount;
        try {
            userAccount = userAccountService.createDefaultUser(username);
        } catch (AlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with this username already exist");
        }
        return userBaseAuthService.create(userAccount.getUsername(), password);
    }

    @PostMapping("/login/{username}")
    @Transactional
    public LoginResponse login(@PathVariable String username, @RequestParam String password) {
        var foundUserAccount = userAccountService.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with this username doesn't exist");
        }
        var userAccount = foundUserAccount.get();
        try {
            return userBaseAuthService.login(userAccount.getUsername(), getRoles(userAccount), password);
        } catch (LockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is frozen");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Authentication error");
        }
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("true")
    @Transactional
    public LoginResponse refreshToken(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken;
        try {
            jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        } catch (ClassCastException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Need refresh token authentication");
        }
        var username = jwtAuthenticationToken.getName();
        var foundWorkingRefreshToken = workingRefreshTokenRepository.getByOwnerUsername(username);
        if (foundWorkingRefreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Refresh token is not presented in our database");
        }
        var workingRefreshToken = foundWorkingRefreshToken.get();
        var jwtId = jwtAuthenticationToken.getToken().getId();
        if (!workingRefreshToken.getJwtId().equals(jwtId)) {
            workingRefreshTokenRepository.delete(workingRefreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token has compromised. You should log in again");
        }
        var refreshedRoles = userRoleService.getRoles(username);
        return userBaseAuthService.generateLoginResponse(username, refreshedRoles);
    }

    @PostMapping("/logout")
    @PreAuthorize("true")
    @Transactional
    public void logout(Authentication authentication) {
        var username = authentication.getName();
        workingRefreshTokenRepository.deleteByOwnerUsername(username);
    }

}