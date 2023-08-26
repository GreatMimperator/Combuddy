package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.security.TokenService;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.entities.UserBaseAuth;
import ru.combuddy.backend.security.repositories.UserBaseAuthRepository;

import java.text.MessageFormat;
import java.util.List;

@Service
@AllArgsConstructor
public class UserBaseAuthServiceImpl implements UserBaseAuthService {

    private final UserAccountService userAccountService;
    private final UserBaseAuthRepository authenticationRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public LoginResponse create(String username, String password) throws NotExistsException, AlreadyExistsException, AuthenticationException {
        var userAccount = userAccountService.getByUsername(username, "user");
        if (authenticationRepository.existsByUserAccountId(userAccount.getId())) {
            throw new AlreadyExistsException(
                    MessageFormat.format("User account with username {0} already has base auth",
                            username),
                    username);
        }
        var userBaseAuth = new UserBaseAuth(null, userAccount, passwordEncoder.encode(password));
        authenticationRepository.save(userBaseAuth);
        userAccount.setBaseAuth(userBaseAuth);
        return login(userAccount.getUsername(), userAccount.getRole().getName(), password); // AuthenticationException risk. Creation will be rolled back if thrown
    }

    @Override
    public LoginResponse login(String username, Role.RoleName roleName, String password) throws AuthenticationException {
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authenticationToken);
        return generateLoginResponse(username, roleName);
    }

    @Override
    public LoginResponse generateLoginResponse(String username, Role.RoleName roleName) {
        var authorities = List.of(new SimpleGrantedAuthority(roleName.name()));
        var accessToken = tokenService.generateAccessToken(username, authorities);
        var refreshToken = tokenService.generateRefreshToken(username);
        return new LoginResponse(accessToken, refreshToken);
    }
}
