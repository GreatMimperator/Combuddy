package ru.combuddy.backend.controllers.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.verifiers.users.UsersDeleteVerifier;
import ru.combuddy.backend.security.verifiers.users.UsersFreezeVerifier;
import ru.combuddy.backend.security.verifiers.users.role.RoleDecreaseVerifier;
import ru.combuddy.backend.security.verifiers.users.role.RoleIncreaseVerifier;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/account")
@AllArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    private final UsersDeleteVerifier deleteVerifier;
    private final UsersFreezeVerifier freezeVerifier;
    private final RoleIncreaseVerifier roleIncreaseVerifier;
    private final RoleDecreaseVerifier roleDecreaseVerifier;

    private final Role.RoleName.AuthorityComparator authorityComparator;


    @PostMapping("/freeze/{suspectUsername}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeze(@PathVariable String suspectUsername, Authentication authentication) {
        setFrozenState(true, suspectUsername, authentication);
    }

    @PostMapping("/unfreeze/{suspectUsername}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable String suspectUsername, Authentication authentication) {
        setFrozenState(false, suspectUsername, authentication);
    }

    private void setFrozenState(boolean frozen, String suspectUsername, Authentication authentication)
            throws ResponseStatusException {
        var suspenderUsername = authentication.getName();
        if (suspectUsername.equals(suspenderUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't freeze yourself");
        }
        UserAccount suspender, suspect;
        try {
            suspender = userAccountService.getByUsername(suspenderUsername, "suspender");
            suspect = userAccountService.getByUsername(suspectUsername, "suspect");
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        if (freezeVerifier.verify(suspender, suspect)) {
            userAccountService.updateFrozenState(frozen, suspectUsername);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not freeze this account");
        }
    }

    @DeleteMapping("/delete/{suspectUsername}")
    @PreAuthorize("hasRole('MAIN_MODERATOR')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String suspectUsername, Authentication authentication) {
        var suspenderUsername = authentication.getName();
        UserAccount suspender, suspect;
        try {
            suspender = userAccountService.getByUsername(suspenderUsername, "suspender");
            suspect = userAccountService.getByUsername(suspectUsername, "suspect");
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        if (deleteVerifier.verify(suspender, suspect)) {
            userAccountService.delete(suspectUsername);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not delete this account");
        }
    }

    @GetMapping("/usernamesBeginWith/{beginPart}")
    public UsernamesList getUsernamesBeginWith(@PathVariable String beginPart) {
        return new UsernamesList(userAccountService.findUsernamesStartedWith(beginPart));
    }

    @PutMapping("/role/set/{roleStringName}/to/{receiverUsername}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setRole(@PathVariable String roleStringName, @PathVariable String receiverUsername, Authentication authentication) {
        String issuerUsername = authentication.getName();
        if (receiverUsername.equals(issuerUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't change role for yourself");
        }
        Role.RoleName roleNameToSet;
        try {
            roleNameToSet = Role.RoleName.convertToRoleName(roleStringName);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Role with this name does not exist");
        }
        UserAccount issuer, receiver;
        try {
            issuer = userAccountService.getByUsername(issuerUsername, "role issuer");
            receiver = userAccountService.getByUsername(receiverUsername, "role receiver");
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        if (authorityComparator.compare(roleNameToSet, receiver.getRole().getName()) >= 0) {
            var verifyInfo = new RoleIncreaseVerifier.VerifyInfo(receiver, roleNameToSet);
            if (roleIncreaseVerifier.verify(issuer, verifyInfo)) {
                userAccountService.replaceRole(receiver, roleNameToSet);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You can not increase the role for this account");
            }
        } else {
            var verifyInfo = new RoleDecreaseVerifier.VerifyInfo(receiver, roleNameToSet);
            if (roleDecreaseVerifier.verify(issuer, verifyInfo)) {
                userAccountService.replaceRole(receiver, roleNameToSet);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You can not decrease the role for this account");
            }
        }
    }


    /**
     * @throws ResponseStatusException if foundUserAccount is empty
     * @return unwrapped userAccount
     */
    public static UserAccount checkFoundAccount(Optional<UserAccount> foundUserAccount) throws ResponseStatusException {
        if (foundUserAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return foundUserAccount.get();
    }
}
