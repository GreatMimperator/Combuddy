package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/user/account")
@AllArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;


    @PostMapping("/freeze/{suspectUsername}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeze(@PathVariable String suspectUsername, Authentication authentication) {
        var suspenderUsername = getUsername(authentication);
        userAccountService.freeze(suspectUsername, suspenderUsername);
    }

    @PostMapping("/unfreeze/{suspectUsername}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable String suspectUsername, Authentication authentication) {
        var suspenderUsername = getUsername(authentication);
        userAccountService.unfreeze(suspectUsername, suspenderUsername);
    }

    @DeleteMapping("/delete/{suspectUsername}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String suspectUsername, Authentication authentication) {
        var suspenderUsername = getUsername(authentication);
        userAccountService.delete(suspenderUsername, suspectUsername);
    }

    @GetMapping("/usernamesBeginWith/{beginPart}/page/{pageNumber}")
    public UsernamesList getUsernamesBeginWith(@PathVariable String beginPart,
                                               @PathVariable int pageNumber) {
        return new UsernamesList(userAccountService.findUsernamesStartedWith(beginPart, pageNumber));
    }

    @PutMapping("/role/set/{roleStringName}/to/{receiverUsername}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setRole(@PathVariable String roleStringName,
                        @PathVariable String receiverUsername,
                        Authentication authentication) {
        String issuerUsername = getUsername(authentication);
        userAccountService.setRole(roleStringName, receiverUsername, issuerUsername);
    }
}
