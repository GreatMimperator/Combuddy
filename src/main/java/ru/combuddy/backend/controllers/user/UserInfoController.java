package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.projections.info.PublicInfoUserInfoProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;

@RestController
@RequestMapping("/api/user/info")
@AllArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/publicInfo/{username}")
    public PublicInfoUserInfoProjection getPublicInfo(@PathVariable String username) {
        var foundPublicInfo = userInfoService.getPublicInfo(username);
        if (foundPublicInfo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return foundPublicInfo.get();
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
