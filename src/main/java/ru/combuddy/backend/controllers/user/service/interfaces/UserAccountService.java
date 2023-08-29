package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.util.StringUtils;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    UserAccount createDefaultUser(String username) throws AlreadyExistsException;

    Optional<Role> findRoleByUsername(String username);

    boolean exists(String username);

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findByUsername(String username);

    /**
     * Calls {@link #findByUsername(String)} and calls {@link #throwUserNotExists(String, String)} on empty.
     * Else returns got user
     *
     * @param alias will be used capitalized in exception
     * @throws NotExistsException thrown via {@link #throwUserNotExists(String, String)}
     */
    default UserAccount getByUsername(String username, String alias) throws NotExistsException {
        var foundUser = findByUsername(username);
        if (foundUser.isEmpty()) {
            throwUserNotExists(username, alias);
        }
        return foundUser.get();
    }

    /**
     * @param alias will be used capitalized in exception
     * @throws NotExistsException this function purpose is to throw this exception
     */
    default void throwUserNotExists(String username, String alias) throws NotExistsException {
        throw new NotExistsException(
                MessageFormat.format("{0} with username {1} does not exist",
                        StringUtils.capitalize(alias),
                        username),
                username);
    }

    /**
     * @throws NotExistsException if account not found
     */
    void updateFrozenState(boolean frozen, String username) throws NotExistsException;

    boolean delete(String username);

    List<String> findUsernamesStartedWith(String usernameBeginPart);

    boolean isFrozen(String username) throws NotExistsException;

    void replaceRole(UserAccount receiver, Role.RoleName roleName);
}
