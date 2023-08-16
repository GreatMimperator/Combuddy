package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;

@RestController
@RequestMapping("/api/user/blacklist")
@AllArgsConstructor
public class BlackListController {

    private final BlackListService blackListService;

    @PutMapping("/add/{aggressorUsername}/of/{defendedUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable String aggressorUsername, @PathVariable String defendedUsername) {
        var added = blackListService.add(aggressorUsername, defendedUsername);
        if (!added) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aggressor or defended username not found");
        }
    }

    @DeleteMapping("/remove/{aggressorUsername}/of/{defendedUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String aggressorUsername, @PathVariable String defendedUsername) {
        var removed = blackListService.remove(aggressorUsername, defendedUsername);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aggressor or defended username not found");
        }
    }

    @GetMapping("/aggressors/{defendedUsername}")
    public UsernamesList getAggressorsUsernames(@PathVariable String defendedUsername) {
        var foundAggressors = blackListService.getAggressorUsernames(defendedUsername);
        if (foundAggressors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return new UsernamesList(foundAggressors.get());
    }
}
