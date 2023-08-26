package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.io.IOException;
import java.util.Optional;

public interface UserInfoService {
    Optional<byte[]> getThumbnailBytes(String username);

    Optional<byte[]> getFullPictureBytes(String username);

    /**
     * @throws ResponseStatusException <br>
     * If picture type is not supported <br>
     * If picture has illegal resolution <br>
     * If converted picture or its thumbnail has too big resolution
     * @throws IOException on picture manipulation exceptions
     */
    void addFullAndThumbnailPictures(UserInfo userInfo, MultipartFile imageMultipartFile) throws ResponseStatusException, IOException;

    /**
     * @throws NotExistsException if user with this username doesn't exist
     */
    UserProfileInfo getAllInfo(String targetUsername, String askerUsername) throws NotExistsException;

    UserInfo save(UserInfo userInfo);
}
