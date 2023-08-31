package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/user/blacklist")
@AllArgsConstructor
public class BlackListController {

    private final BlackListService blackListService;

    @PutMapping("/add/{aggressorUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable String aggressorUsername, Authentication authentication) {
        var defendedUsername = getUsername(authentication);
        blackListService.add(aggressorUsername, defendedUsername);
    }

    @DeleteMapping("/delete/{aggressorUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String aggressorUsername, Authentication authentication) {
        var defendedUsername = getUsername(authentication);
        blackListService.delete(aggressorUsername, defendedUsername);
    }

    @GetMapping("/aggressors")
    public UsernamesList getAggressorsUsernames(Authentication authentication) {
        var defendedUsername = getUsername(authentication);
        var aggressorsUsernames = blackListService.getAggressorUsernames(defendedUsername);
        return new UsernamesList(aggressorsUsernames);
    }
}
