package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.controllers.user.service.interfaces.UserBaseAuthService;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.user.UserAccountRepository;
import ru.combuddy.backend.security.TokenService;
import ru.combuddy.backend.security.BaseAuthUserDetailsService;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.entities.UserBaseAuth;
import ru.combuddy.backend.security.repositories.UserBaseAuthRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UserBaseAuthServiceImpl implements UserBaseAuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserBaseAuthRepository authenticationRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final BaseAuthUserDetailsService userDetailsService;

    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public LoginResponse create(String username, String password) throws NotExistsException, AlreadyExistsException, AuthenticationException {
        var foundUserAccount = userAccountRepository.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throw new NotExistsException("User account with username %s doesn't exist".formatted(username));
        }
        var userAccount = foundUserAccount.get();
        if (authenticationRepository.existsByUserAccountId(userAccount.getId())) {
            throw new AlreadyExistsException("User account with username %s already has base auth".formatted(username));
        }
        var userBaseAuth = new UserBaseAuth(null, userAccount, passwordEncoder.encode(password));
        authenticationRepository.save(userBaseAuth);
        userAccount.setBaseAuth(userBaseAuth);
        return login(userAccount.getUsername(), userAccount.getRoles(), password); // AuthenticationException risk. Creation will be rolled back if thrown
    }

    @Override
    public LoginResponse login(String username, Collection<Role> roles, String password) throws AuthenticationException {
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authenticationToken);
        return generateLoginResponse(username, roles);
    }

    @Override
    public LoginResponse generateLoginResponse(String username, Collection<Role> roles) {
        var authorities = roles.stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var accessToken = tokenService.generateAccessToken(username, authorities);
        var refreshToken = tokenService.generateRefreshToken(username);
        return new LoginResponse(accessToken, refreshToken);
    }
}
