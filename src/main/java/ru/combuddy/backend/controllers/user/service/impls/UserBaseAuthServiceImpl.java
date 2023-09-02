package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.exceptions.user.UserAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.authentication.UserAuthDataExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.exceptions.user.authentication.AccountIsFrozenException;
import ru.combuddy.backend.exceptions.user.authentication.CompromisedRefreshTokenException;
import ru.combuddy.backend.exceptions.user.authentication.RefreshTokenNotFoundException;
import ru.combuddy.backend.exceptions.user.authentication.UserAuthenticationException;
import ru.combuddy.backend.security.RoleName;
import ru.combuddy.backend.security.TokenService;
import ru.combuddy.backend.security.entities.UserBaseAuth;
import ru.combuddy.backend.security.repositories.UserBaseAuthRepository;
import ru.combuddy.backend.security.repositories.WorkingRefreshTokenRepository;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserBaseAuthServiceImpl implements UserBaseAuthService {

    private final UserBaseAuthRepository authenticationRepository;
    private final WorkingRefreshTokenRepository workingRefreshTokenRepository;
    private final UserAccountService userAccountService;
    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse create(String username, String password)
            throws UserNotExistsException,
            UserAuthDataExistsException,
            AccountIsFrozenException,
            UserAuthenticationException {
        var userAccount = userAccountService.getByUsername(username);
        if (authenticationRepository.existsByUserAccountId(userAccount.getId())) {
            throw new UserAuthDataExistsException("User already has base auth");
        }
        var userBaseAuth = new UserBaseAuth(null, userAccount, passwordEncoder.encode(password));
        authenticationRepository.save(userBaseAuth);
        userAccount.setBaseAuth(userBaseAuth);
        return login(userAccount.getUsername(), userAccount.getRoleName(), password); // AuthenticationException risk. Creation will be rolled back if thrown
    }

    @Override
    public LoginResponse login(String username,
                               RoleName role,
                               String password)
            throws AccountIsFrozenException,
            UserAuthenticationException {
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (LockedException e) {
            throw new AccountIsFrozenException("Account is frozen");
        } catch (AuthenticationException e) {
            throw new UserAuthenticationException("Login has failed", e);
        }
        return generateLoginResponse(username, role);
    }

    @Override
    public LoginResponse login(String username, String password)
            throws UserNotExistsException,
            AccountIsFrozenException,
            UserAuthenticationException {
        var account = userAccountService.getByUsername(username);
        return this.login(account.getUsername(), account.getRoleName(), password);
    }

    @Override
    public LoginResponse generateLoginResponse(String username, RoleName roleName) {
        var authorities = List.of(new SimpleGrantedAuthority(roleName.name()));
        var accessToken = tokenService.generateAccessToken(username, authorities);
        var refreshToken = tokenService.generateRefreshToken(username);
        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse registerUser(String username, String password)
            throws UserAlreadyExistsException,
            UserAuthDataExistsException,
            AccountIsFrozenException,
            UserAuthenticationException {
        var userAccount = userAccountService.createDefaultUser(username);
        return this.create(userAccount.getUsername(), password);
    }

    @Override
    public LoginResponse refreshToken(JwtAuthenticationToken jwtRefreshToken, String username)
            throws RefreshTokenNotFoundException,
            CompromisedRefreshTokenException,
            UserNotExistsException {
        var foundWorkingRefreshToken = workingRefreshTokenRepository.findByOwnerUsername(username);
        if (foundWorkingRefreshToken.isEmpty()) {
            throw new RefreshTokenNotFoundException("Refresh token not found in our db");
        }
        var workingRefreshToken = foundWorkingRefreshToken.get();
        var jwtId = jwtRefreshToken.getToken().getId();
        if (!workingRefreshToken.getJwtId().equals(jwtId)) {
            workingRefreshTokenRepository.delete(workingRefreshToken);
            throw new CompromisedRefreshTokenException("Given refresh token is not equal to latest refresh token");
        }
        var foundUser = userAccountService.findByUsername(username);
        if (foundUser.isEmpty()) {
            throw new UserNotExistsException("User does not exist");
        }
        var refreshedRoleName = foundUser.get().getRoleName();
        return this.generateLoginResponse(username, refreshedRoleName);
    }

    @Override
    public void logout(String username) {
        workingRefreshTokenRepository.deleteByOwnerUsername(username);
    }
}
