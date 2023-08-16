package ru.combuddy.backend.controllers.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.exceptions.AlreadyExistsException;

import java.io.IOException;

@RestController
@RequestMapping("/api/user/account")
@AllArgsConstructor
public class UserAccountController {

    private final UserInfoService userInfoService;
    private final UserAccountService userAccountService;

    @PostMapping(value = "/create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.MULTIPART_MIXED_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestPart("user") @Valid User user, @RequestPart("picture") MultipartFile imageMultipartFile) throws IOException {
        if (userAccountService.exists(user.getUserAccount().getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with this username already exist"); // to not perform computations
        }
        userInfoService.addFullAndThumbnailPictures(user, imageMultipartFile);
        try {
            userAccountService.createUser(user);
        } catch (AlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with this username already exist");
        }
    }

    @PostMapping("/freeze/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeze(@PathVariable String username) {
        boolean updatedFrozen = userAccountService.updateFrozenState(true, username);
        if (!updatedFrozen) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @PostMapping("/unfreeze/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable String username) {
        boolean updatedFrozen = userAccountService.updateFrozenState(false, username);
        if (!updatedFrozen) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @DeleteMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String username) {
        boolean deleted = userAccountService.delete(username);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @GetMapping("/usernamesBeginWith/{beginPart}")
    public UsernamesList getUsernamesBeginWith(@PathVariable String beginPart) {
        return new UsernamesList(userAccountService.findUsernamesStartedWith(beginPart));
    }

}
