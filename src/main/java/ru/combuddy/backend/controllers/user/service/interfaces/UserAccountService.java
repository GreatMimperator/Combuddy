package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    UserAccount createDefaultUser(String username) throws AlreadyExistsException;

    Optional<UserAccount> findByUsername(String username);

    /**
     * @throws NotExistsException if account not found
     */
    void updateFrozenState(boolean frozen, String username) throws NotExistsException;

    boolean exists(String username);

    /**
     * @throws NotExistsException if account not found
     */
    void delete(String username) throws NotExistsException;

    List<String> findUsernamesStartedWith(String usernameBeginPart);

    boolean isFrozen(String username);
}
