package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UserPublicInfo;
import ru.combuddy.backend.controllers.user.projections.info.FullPictureProjection;
import ru.combuddy.backend.controllers.user.projections.info.ThumbnailProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.util.ImageConverter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.combuddy.backend.entities.user.UserAccount.getRoles;

@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;
    private UserAccountService userAccountService;

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
    public void addFullAndThumbnailPictures(UserInfo userInfo, MultipartFile imageMultipartFile) throws ResponseStatusException, IOException {
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
        byte[] thumbnailPngBytes;
        try {
            userInfo.setFullPicture(pngData.getBytes());
            thumbnailPngBytes = ImageConverter.resizeImage(pngData.getBytes(),
                    UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                    UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                    "png");
            userInfo.setPictureThumbnail(thumbnailPngBytes);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "Too big picture of its generated thumbnail");
        }
    }

    @Override
    public UserPublicInfo getPublicInfo(String username) throws NotExistsException {
        var foundUserAccount = userAccountService.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throw new NotExistsException("User account with username %s doesn't exist".formatted(username)); // todo: formatted -> MessageFormat
        }
        var userAccount = foundUserAccount.get();
        var subscriptions = userAccount.getSubscriptions().stream()
                .map(Subscription::getPoster)
                .map(UserAccount::getUsername)
                .toList();
        var roles = getRoles(userAccount).stream()
                .map(Role::getName)
                .toList();
        return UserPublicInfo.builder()
                .username(username)
                .frozen(userAccount.getFrozen())
                .registeredDate(userAccount.getUserInfo().getRegisteredDate())
                .roles(roles)
                .subscriptions(subscriptions)
                .build();
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }
}
