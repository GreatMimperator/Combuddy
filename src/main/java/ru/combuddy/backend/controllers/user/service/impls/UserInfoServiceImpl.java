package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.controllers.user.projections.info.FullPictureProjection;
import ru.combuddy.backend.controllers.user.projections.info.ThumbnailProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.user.PrivacyPolicyRepository;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.security.verifiers.users.info.RegisteredDateAccessVerifier;
import ru.combuddy.backend.security.verifiers.users.info.SubscriptionsAccessVerifier;
import ru.combuddy.backend.util.ImageConverter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;
    private UserAccountService userAccountService;
    private SubscriptionService subscriptionService;
    private PrivacyPolicyRepository privacyPolicyRepository;

    private SubscriptionsAccessVerifier subscriptionsAccessVerifier;
    private RegisteredDateAccessVerifier registeredDateAccessVerifier;

    @Transactional
    @Override
    public Optional<byte[]> getThumbnailBytes(String username) {
        return userInfoRepository.findThumbnailByUserAccountUsername(username)
                .map(ThumbnailProjection::getPictureThumbnail);
    }

    @Transactional
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

    @Transactional
    @Override
    public UserProfileInfo getAllInfo(String targetUsername, String askerUsername) throws NotExistsException {
        var targetAccount = userAccountService.getByUsername(targetUsername, "target account");
        var askerAccount = userAccountService.getByUsername(askerUsername, "asker account");
        var privacyPolicy = privacyPolicyRepository
                .findByUserAccountId(targetAccount.getId()).get();
        var roleName = targetAccount.getRole().getName();
        var builder = UserProfileInfo.builder()
                .username(targetUsername)
                .frozen(targetAccount.getFrozen())
                .role(roleName.name());
        var permittedToSee = new UserProfileInfo.PermittedToSee();
        builder.permittedToSee(permittedToSee);
        var subscriptionVerifyInfo = new SubscriptionsAccessVerifier.VerifyInfo(
                targetAccount,
                privacyPolicy.getSubscriptionsAccessLevel());
        if (subscriptionsAccessVerifier.verify(askerAccount, subscriptionVerifyInfo)) {
            permittedToSee.setSubscriptions(true);
            var subscriptions = subscriptionService.getPosterUsernames(targetUsername);
            builder.subscriptions(Optional.of(subscriptions));
        }
        var registeredDateVerifyInfo = new RegisteredDateAccessVerifier.VerifyInfo(
                targetAccount,
                privacyPolicy.getRegisteredDateAccessLevel());
        if (registeredDateAccessVerifier.verify(askerAccount, registeredDateVerifyInfo)) {
            permittedToSee.setRegisteredDate(true);
            builder.registeredDate(Optional.of(targetAccount.getUserInfo().getRegisteredDate()));
        }
        return builder.build();
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }
}
