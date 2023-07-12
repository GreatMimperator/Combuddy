package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    /**
     * @return false if not unique username, true if created
     */
    boolean createUser(User user);

    Optional<UserAccount> findByUsername(String username);

    /**
     * @return false if not found, true if found and updated
     */
    boolean updateFrozenState(boolean frozen, String username);

    boolean exists(String username);

    /**
     * @return false if not found, true if deleted
     */
    boolean delete(String username);

    List<String> findUsernamesStartedWith(String usernameBeginPart);
}
