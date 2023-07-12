package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.controllers.user.projections.info.PublicInfoUserInfoProjection;

import java.io.IOException;
import java.util.Optional;

public interface UserInfoService {
    Optional<byte[]> getThumbnailBytes(String username);

    Optional<byte[]> getFullPictureBytes(String username);

    void addFullAndThumbnailPictures(User user, MultipartFile imageMultipartFile) throws ResponseStatusException, IOException;

    Optional<PublicInfoUserInfoProjection> getPublicInfo(String username);
}
