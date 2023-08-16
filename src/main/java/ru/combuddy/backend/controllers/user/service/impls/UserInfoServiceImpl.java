package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.controllers.user.projections.info.FullPictureProjection;
import ru.combuddy.backend.controllers.user.projections.info.PublicInfoUserInfoProjection;
import ru.combuddy.backend.controllers.user.projections.info.ThumbnailProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.util.ImageConverter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;

    @Override
    public Optional<byte[]> getThumbnailBytes(String username) {
        return userInfoRepository.findThumbnailByUserAccountUsername(username)
                .map(ThumbnailProjection::getPictureThumbnail);
    }

    @Override
    public Optional<byte[]> getFullPictureBytes(String username) {
        return userInfoRepository.findFullPictureByUserAccountUsername(username)
                .map(FullPictureProjection::getFullPicture);
    }

    @Override
    public void addFullAndThumbnailPictures(User user, MultipartFile imageMultipartFile) throws ResponseStatusException, IOException {
        var pngData = ImageConverter.convertImage(imageMultipartFile.getBytes(), "png");
        if (pngData == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Picture has unsupported extension");
        }
        if (!UserInfo.isFullPictureSize(pngData.getWidth(), pngData.getHeight())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageFormat.format("Picture should have resolution {0}x{1} px",
                            UserInfo.PICTURE_FULL_PICTURE_SIZE_PX,
                            UserInfo.PICTURE_FULL_PICTURE_SIZE_PX));
        }
        var userInfo = user.getUserInfo();
        byte[] thumbnailPngBytes;
        try {
            userInfo.setFullPicture(pngData.getBytes());
            thumbnailPngBytes = ImageConverter.resizeImage(pngData.getBytes(),
                    UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                    UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                    "png");
            userInfo.setPictureThumbnail(thumbnailPngBytes);
        } catch (ValidationException | IOException e) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Too big picture of its generated thumbnail");
        }
    }

    @Override
    public Optional<PublicInfoUserInfoProjection> getPublicInfo(String username) {
        return userInfoRepository.findPublicUserInfoByUserAccountUsername(username);
    }
}
