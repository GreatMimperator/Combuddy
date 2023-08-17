package ru.combuddy.backend.controllers.user;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.security.entities.Role;

import java.util.Set;

@RestController
@RequestMapping("/api/user/auth")
@AllArgsConstructor
public class BaseAuthController {

    private final UserAccountService userAccountService;
    private final UserBaseAuthService userBaseAuthService;

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
            return userBaseAuthService.login(userAccount.getUsername(), userAccount.getRoles(), password);
        } catch (LockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is frozen");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Authentication error");
        }
    }

    @PostMapping("/refresh-token")
    public LoginResponse refreshToken(@RequestParam String refreshToken) {

    }
}