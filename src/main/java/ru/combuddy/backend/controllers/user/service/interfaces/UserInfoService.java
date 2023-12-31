package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.controllers.user.models.PrivacyPolicyInfo;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.files.FIleWeightException;
import ru.combuddy.backend.exceptions.files.UnsupportedPictureException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.io.IOException;
import java.io.InputStream;

public interface UserInfoService {
    InputStream getProfilePictureThumbnail(String username);

    InputStream getFullProfilePicture(String username) throws UserNotExistsException;

    /**
     * @throws UnsupportedPictureException if picture type is not supported, or picture has illegal resolution
     * @throws FIleWeightException if converted picture or its thumbnail has too big resolution
     * @throws IOException on picture manipulation exceptions
     */
    void addFullAndThumbnailPictures(UserInfo userInfo,
                                     InputStream pictureStream)
            throws IOException,
            UnsupportedPictureException,
            FIleWeightException;

    UserProfileInfo getAllInfo(String targetUsername, String askerUsername)
                    throws UserNotExistsException;

    UserInfo save(UserInfo userInfo);

    /**
     * @throws UnsupportedPictureException if picture type is not supported, or picture has illegal resolution
     * @throws FIleWeightException if converted picture or its thumbnail has too big resolution
     * @throws IOException on picture manipulation exceptions
     */
    void setProfilePicture(InputStream pictureInputStream, String username)
            throws IOException,
            UserNotExistsException,
            UnsupportedPictureException,
            FIleWeightException;

    void setPrivacyPolicy(PrivacyPolicyInfo privacyPolicyInfo, String username)
            throws UserNotExistsException;

    void setDefaultPrivacyPolicy(String username)
            throws UserNotExistsException;

    static PrivacyPolicyInfo getDefaultPrivacyPolicyInfo() {
        return new PrivacyPolicyInfo(
                PrivacyPolicy.RegisteredDateAccessLevel.EVERYBODY,
                PrivacyPolicy.SubscriptionsAccessLevel.EVERYBODY
        );
    }

    static void setPrivacyPolicyToDefault(PrivacyPolicy privacyPolicy) {
        var defaultPrivacyPolicyInfo = getDefaultPrivacyPolicyInfo();
        privacyPolicy.setSubscriptionsAccessLevel(defaultPrivacyPolicyInfo.getSubscriptionsAccessLevel());
        privacyPolicy.setRegisteredDateAccessLevel(defaultPrivacyPolicyInfo.getRegisteredDateAccessLevel());
    }

    PrivacyPolicyInfo getPrivacyPolicyInfo(String username) throws UserNotExistsException;
}
