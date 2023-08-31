package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.general.IllegalPageNumberException;
import ru.combuddy.backend.exceptions.permission.FreezeStateSetNotPermittedException;
import ru.combuddy.backend.exceptions.permission.user.RoleSetNotPermittedException;
import ru.combuddy.backend.exceptions.user.InvalidRoleNameException;
import ru.combuddy.backend.exceptions.user.UserAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.security.entities.Role;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    UserAccount createDefaultUser(String username) throws UserAlreadyExistsException;

    boolean exists(String username);

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findByUsername(String username);

    UserAccount getByUsername(String username) throws UserNotExistsException;

    Optional<Role> findRoleByUsername(String username);

    boolean delete(String username);

    List<String> findUsernamesStartedWith(String usernameBeginPart, int pageNumberSinceOne) throws IllegalPageNumberException;

    boolean isFrozen(String username) throws UserNotExistsException;

    void replaceRole(UserAccount receiver, Role.RoleName roleName);

    void freeze(String suspectUsername, String suspenderUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException;

    void unfreeze(String suspectUsername, String suspenderUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException;

    void delete(String suspenderUsername, String suspectUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException;

    void setRole(String roleStringName,
                 String receiverUsername,
                 String issuerUsername)
            throws UserNotExistsException,
            InvalidRoleNameException,
            RoleSetNotPermittedException;
}
