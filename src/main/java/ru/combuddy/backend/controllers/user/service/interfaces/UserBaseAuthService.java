package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.security.entities.Role;

import java.util.Collection;
import java.util.Optional;

public interface UserBaseAuthService {

    /**
     * @return loginResponse of created account
     * @throws AuthenticationException thrown inside by {@link AuthenticationManager#authenticate(Authentication)} (not expected to be thrown)
     * @throws NotExistsException if user account with this id not exists
     * @throws AlreadyExistsException if user account already has base auth
     */
    LoginResponse create(String username, String password) throws NotExistsException, AlreadyExistsException, AuthenticationException;

    /**
     * @return loginResponse if password corresponds db password for this user
     * @throws AuthenticationException thrown inside by {@link AuthenticationManager#authenticate(Authentication)}
     */
    LoginResponse login(String username, Collection<Role> roles, String password) throws AuthenticationException;
}
