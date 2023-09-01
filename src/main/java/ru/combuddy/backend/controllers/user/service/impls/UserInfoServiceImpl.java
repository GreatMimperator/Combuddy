package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.user.models.PrivacyPolicyInfo;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.files.FIleWeightException;
import ru.combuddy.backend.exceptions.files.UnsupportedPictureException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.user.PrivacyPolicyRepository;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.security.verifiers.users.info.RegisteredDateAccessVerifier;
import ru.combuddy.backend.security.verifiers.users.info.SubscriptionsAccessVerifier;
import ru.combuddy.backend.util.ImageConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final PrivacyPolicyRepository privacyPolicyRepository; // todo: in service?
    private final UserAccountService userAccountService;
    private final SubscriptionService subscriptionService;
    private final UserContactService userContactService;

    private final SubscriptionsAccessVerifier subscriptionsAccessVerifier;
    private final RegisteredDateAccessVerifier registeredDateAccessVerifier;

    @Override
    public InputStream getProfilePictureThumbnail(String username) {
        var foundThumbnail = userInfoRepository.findThumbnailByUserAccountUsername(username);
        if (foundThumbnail.isEmpty()) {
            throw new UserNotExistsException("User that profile full picture requested does not exist");
        }
        return new ByteArrayInputStream(foundThumbnail.get().getPictureThumbnail());
    }

    @Override
    public InputStream getFullProfilePicture(String username) throws UserNotExistsException {
        var foundFullPicture = userInfoRepository.findFullPictureByUserAccountUsername(username);
        if (foundFullPicture.isEmpty()) {
            throw new UserNotExistsException("User that profile full picture requested does not exist");
        }
        return new ByteArrayInputStream(foundFullPicture.get().getFullPicture());
    }

    @Override
    public void addFullAndThumbnailPictures(UserInfo userInfo,
                                            InputStream pictureStream)
            throws IOException,
            UnsupportedPictureException,
            FIleWeightException {
        var pngData = ImageConverter.convertImage(pictureStream, "png");
        if (pngData == null) {
            throw new UnsupportedPictureException("Picture has unsupported extension");
        }
        if (!UserInfo.isFullPictureSize(pngData.getWidth(), pngData.getHeight())) {
            throw new UnsupportedPictureException(
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
            throw new FIleWeightException("Too big full picture or picture of its generated thumbnail");
        }
    }

    @Override
    public UserProfileInfo getAllInfo(String targetUsername, String askerUsername)
            throws UserNotExistsException {
        var targetAccount = userAccountService.getByUsername(targetUsername);
        var askerAccount = userAccountService.getByUsername(askerUsername);
        var privacyPolicy = privacyPolicyRepository
                .findByUserAccountId(targetAccount.getId()).get();
        var roleName = targetAccount.getRole().getName();
        var baseContacts = userContactService.toBaseContacts(targetAccount.getContacts());
        var builder = UserProfileInfo.builder()
                .username(targetUsername)
                .frozen(targetAccount.getFrozen())
                .role(roleName.name())
                .contacts(baseContacts);
        var subscriptionVerifyInfo = new SubscriptionsAccessVerifier.VerifyInfo(
                targetAccount,
                privacyPolicy.getSubscriptionsAccessLevel());
        if (subscriptionsAccessVerifier.verify(askerAccount, subscriptionVerifyInfo)) {
            var subscriptions = subscriptionService.getPosterUsernames(targetUsername);
            builder.subscriptions(Optional.of(subscriptions));
        }
        var registeredDateVerifyInfo = new RegisteredDateAccessVerifier.VerifyInfo(
                targetAccount,
                privacyPolicy.getRegisteredDateAccessLevel());
        if (registeredDateAccessVerifier.verify(askerAccount, registeredDateVerifyInfo)) {
            builder.registeredDate(Optional.of(targetAccount.getUserInfo().getRegisteredDate()));
        }
        return builder.build();
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    @Override
    public void setProfilePicture(InputStream pictureInputStream, String username)
            throws IOException,
            UserNotExistsException,
            UnsupportedPictureException,
            FIleWeightException {
        var userAccount = userAccountService.getByUsername(username);
        var userInfo = userAccount.getUserInfo();
        this.addFullAndThumbnailPictures(userInfo, pictureInputStream);
        this.save(userInfo);
    }

    @Override
    public void setPrivacyPolicy(PrivacyPolicyInfo privacyPolicyInfo, String username) throws UserNotExistsException {
        var user = userAccountService.getByUsername(username);
        var privacyPolicy = privacyPolicyRepository.findByUserAccountId(user.getId()).get();
        privacyPolicy.setSubscriptionsAccessLevel(privacyPolicyInfo.getSubscriptionsAccessLevel());
        privacyPolicy.setRegisteredDateAccessLevel(privacyPolicyInfo.getRegisteredDateAccessLevel());
        privacyPolicyRepository.save(privacyPolicy);
    }

    @Override
    public void setDefaultPrivacyPolicy(String username) throws UserNotExistsException {
        var user = userAccountService.getByUsername(username);
        var privacyPolicy = privacyPolicyRepository.findByUserAccountId(user.getId()).get();
        UserInfoService.setPrivacyPolicyToDefault(privacyPolicy);
        privacyPolicyRepository.save(privacyPolicy);
    }

    @Override
    public PrivacyPolicyInfo getPrivacyPolicyInfo(String username) throws UserNotExistsException {
        var userAccount = userAccountService.getByUsername(username);
        var privacyPolicy = privacyPolicyRepository.findByUserAccountId(userAccount.getId()).get();
        return new PrivacyPolicyInfo(privacyPolicy);
    }
}
