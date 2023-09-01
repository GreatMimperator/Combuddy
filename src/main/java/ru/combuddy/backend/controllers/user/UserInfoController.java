package ru.combuddy.backend.controllers.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.combuddy.backend.controllers.user.models.PrivacyPolicyInfo;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;

import java.io.IOException;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/user/info")
@AllArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @PostMapping(value = "/set-profile-picture",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.MULTIPART_MIXED_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void setProfilePicture(@RequestPart("picture") MultipartFile pictureMultipartFile,
                                  Authentication authentication)
            throws IOException {
        var username = getUsername(authentication);
        var pictureInputStream = pictureMultipartFile.getInputStream();
        userInfoService.setProfilePicture(pictureInputStream, username);
    }

    @GetMapping("/all/{profileOwnerUsername}")
    public UserProfileInfo getProfilePublicInfo(@PathVariable String profileOwnerUsername, Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        return userInfoService.getAllInfo(profileOwnerUsername, receiverUsername);
    }

    @GetMapping(value = "/thumbnail/{username}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public InputStreamResource getThumbnail(@PathVariable String username) {
        return new InputStreamResource(userInfoService.getProfilePictureThumbnail(username));
    }

    @GetMapping(value = "/full-picture/{username}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public InputStreamResource getFullPicture(@PathVariable String username) {
        return new InputStreamResource(userInfoService.getFullProfilePicture(username));
    }

    @PutMapping("/privacy-policy/set")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPrivacyPolicy(@Valid @RequestBody PrivacyPolicyInfo privacyPolicyInfo,
                                 Authentication authentication) {
        var username = getUsername(authentication);
        userInfoService.setPrivacyPolicy(privacyPolicyInfo, username);
    }

    @PutMapping("/privacy-policy/set-default")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPrivacyPolicy(Authentication authentication) {
        var username = getUsername(authentication);
        userInfoService.setDefaultPrivacyPolicy(username);
    }

    @GetMapping("/privacy-policy/get")
    public PrivacyPolicyInfo getPrivacyPolicy(Authentication authentication) {
        var username = getUsername(authentication);
        return userInfoService.getPrivacyPolicyInfo(username);
    }
}
