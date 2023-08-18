package ru.combuddy.backend.controllers.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UserPublicInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.io.IOException;

import static ru.combuddy.backend.controllers.user.UserAccountController.checkFoundAccount;

@RestController
@RequestMapping("/api/user/info")
@AllArgsConstructor
public class UserInfoController {

    private final UserAccountService userAccountService;
    private final UserInfoService userInfoService;

    @PutMapping(value = "/set-profile-picture",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.MULTIPART_MIXED_VALUE})
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public void setProfilePicture(@RequestPart("picture") MultipartFile imageMultipartFile, Authentication authentication) throws IOException {
        var username = authentication.getName();
        var userAccount = checkFoundAccount(userAccountService.findByUsername(username));
        var userInfo = userAccount.getUserInfo();
        userInfoService.addFullAndThumbnailPictures(userInfo, imageMultipartFile);
        userInfoService.save(userInfo);
    }

    @GetMapping("/publicInfo/{username}")
    public UserPublicInfo getPublicInfo(@PathVariable String username) {
        try {
            return userInfoService.getPublicInfo(username);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @GetMapping(value = "/thumbnail/{username}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getThumbnail(@PathVariable String username) {
        var foundThumbnailBytes = userInfoService.getThumbnailBytes(username);
        if (foundThumbnailBytes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return foundThumbnailBytes.get();
    }

    @GetMapping(value = "/fullPicture/{username}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getFullPicture(@PathVariable String username) {
        var foundThumbnailBytes = userInfoService.getFullPictureBytes(username);
        if (foundThumbnailBytes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return foundThumbnailBytes.get();
    }
}
