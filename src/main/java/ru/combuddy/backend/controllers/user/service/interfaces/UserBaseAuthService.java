package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.exceptions.user.UserAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.exceptions.user.authentication.*;
import ru.combuddy.backend.security.entities.Role;

public interface UserBaseAuthService {
    LoginResponse create(String username, String password)
        throws UserNotExistsException,
            UserAuthDataExistsException,
            AccountIsFrozenException,
            UserAuthenticationException;

    LoginResponse login(String username,
                        Role.RoleName roleName,
                        String password)
        throws AccountIsFrozenException,
            UserAuthenticationException;

    LoginResponse login(String username, String password)
        throws UserNotExistsException,
            AccountIsFrozenException,
            UserAuthenticationException;

    LoginResponse generateLoginResponse(String username, Role.RoleName roleName);

    LoginResponse registerUser(String username, String password)
        throws UserAlreadyExistsException,
            UserAuthDataExistsException,
            AccountIsFrozenException,
            UserAuthenticationException;

    LoginResponse refreshToken(JwtAuthenticationToken jwtRefreshToken, String username)
        throws RefreshTokenNotFoundException,
            CompromisedRefreshTokenException,
            UserNotExistsException;

    void logout(String username);
}
