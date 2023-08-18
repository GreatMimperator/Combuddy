package ru.combuddy.backend.controllers.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.UserRoleService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.security.entities.Role;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static ru.combuddy.backend.util.RoleUtil.getStringAuthorities;

@RestController
@RequestMapping("/api/user/account")
@AllArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserRoleService userRoleService;

    // todo: test it in postman

    @PostMapping("/freeze/{suspectUsername}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeze(@PathVariable String suspectUsername, Authentication authentication) {
        setFrozenState(true, suspectUsername, authentication);
    }

    @PostMapping("/unfreeze/{suspectUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable String suspectUsername, Authentication authentication) {
        setFrozenState(false, suspectUsername, authentication);
    }

    private void setFrozenState(boolean frozen, String suspectUsername, Authentication authentication)
            throws ResponseStatusException {
        var suspenderAuthorities = getStringAuthorities(authentication);
        var suspectAuthorities = getStringAuthorities(userRoleService.getRoles(suspectUsername));
        if (suspectUsername.equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't freeze yourself");
        }
        if (!canFreezeAccount(suspenderAuthorities, suspectAuthorities)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can't freeze / unfreeze an account that is above you (or the same level) in the hierarchy");
        }
        try {
            userAccountService.updateFrozenState(frozen, suspectUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    public boolean canFreezeAccount(Set<String> suspenderAuthorities, Set<String> suspectAuthorities) {
        // suspender hierarchy markers construct
        boolean isSuspenderModerator = isModerator(suspenderAuthorities);
        boolean isSuspenderMainModerator = isMainModerator(suspenderAuthorities);
        boolean[] suspenderHierarchyMarkers = new boolean[] {
                isSuspenderModerator,
                isSuspenderMainModerator};
        // suspect hierarchy markers construct
        boolean isSuspectModerator = isModerator(suspectAuthorities);
        boolean isSuspectMainModerator = isMainModerator(suspectAuthorities);
        boolean[] suspectHierarchyMarkers = new boolean[] {
                isSuspectModerator,
                isSuspectMainModerator};
        System.err.println(MessageFormat.format("{0} {1}, {2} {3}", isSuspenderModerator, isSuspenderMainModerator, isSuspectModerator, isSuspectMainModerator));
        // comparing hierarchies
        return Role.isAboveInHierarchy(
                suspenderHierarchyMarkers,
                suspectHierarchyMarkers);
    }

    private boolean isMainModerator(Collection<String> authorities) {
        return authorities.contains("ROLE_MAIN_MODERATOR");
    }

    private boolean isModerator(Collection<String> authorities) {
        return authorities.contains("ROLE_MODERATOR");
    }

    @DeleteMapping("/delete/{suspectUsername}")
    @PreAuthorize("hasRole('MAIN_MODERATOR')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String suspectUsername) {
        var suspectAuthorities = getStringAuthorities(userRoleService.getRoles(suspectUsername));
        boolean isSuspenderMainModerator = isMainModerator(suspectAuthorities);
        boolean isSuspenderModerator = isModerator(suspectAuthorities);
        if (isSuspenderMainModerator || isSuspenderModerator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Can not delete moderator or main moderator account via rest api. Do it directly via database");
        }
        try {
            userAccountService.delete(suspectUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @GetMapping("/usernamesBeginWith/{beginPart}")
    public UsernamesList getUsernamesBeginWith(@PathVariable String beginPart) {
        return new UsernamesList(userAccountService.findUsernamesStartedWith(beginPart));
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
