package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;

@RestController
@RequestMapping("/api/user/blacklist")
@AllArgsConstructor
public class BlackListController {

    private final BlackListService blackListService;

    @PutMapping("/add/{aggressorUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable String aggressorUsername, Authentication authentication) {
        var defendedUsername = authentication.getName();
        try {
            blackListService.add(aggressorUsername, defendedUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aggressor or defended username not found");
        } catch (ShouldNotBeEqualException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aggressor and defended usernames should not be equal");
        }
    }

    @DeleteMapping("/remove/{aggressorUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String aggressorUsername, Authentication authentication) {
        var defendedUsername = authentication.getName();
        try {
            blackListService.remove(aggressorUsername, defendedUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aggressor or defended username not found");
        } catch (ShouldNotBeEqualException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aggressor and defended usernames should not be equal");
        }
    }

    @GetMapping("/aggressors")
    public UsernamesList getAggressorsUsernames(Authentication authentication) {
        var defendedUsername = authentication.getName();
        var aggressorsUsernames = blackListService.getAggressorUsernames(defendedUsername);
        return new UsernamesList(aggressorsUsernames);
    }
}
