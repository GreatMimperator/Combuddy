package ru.combuddy.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.queries.user.UserInfoControllerQueries;
import ru.combuddy.backend.util.ImageConverter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoControllerQueries userInfoControllerQueries;

    public static final String PENGUIN_RESOURCES_PICTURE_PATH = "test-samples/Icon_Pinguin_1_512x512.png";


    @Test
    @Transactional
    public void allForRandomUserByModeratorTest() throws Exception {
        var moderatorLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var userUsername = RANDOM_USER_USERNAME;
        var userProfileInfoJson = userInfoControllerQueries.all(userUsername, moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var gotProfileInfo = jsonToUserProfileInfo(userProfileInfoJson);
        var randomUser = userAccountService.findByUsername(userUsername).get();
        assertEquals(gotProfileInfo.getUsername(), randomUser.getUsername());
        assertEquals(gotProfileInfo.isFrozen(), randomUser.getFrozen());
        assertEquals(gotProfileInfo.getRole(), randomUser.getRole().getName().name());
        assertEquals(gotProfileInfo.getRegisteredDate().get().getTimeInMillis(),
                randomUser.getUserInfo().getRegisteredDate().getTimeInMillis());
        var permittedToSee = new UserProfileInfo.PermittedToSee(true, false);
        assert gotProfileInfo.getSubscriptions().isEmpty() &&
                !permittedToSee.isSubscriptions();
        assertEquals(gotProfileInfo.getPermittedToSee(), permittedToSee);
    }

    @Test
    @Transactional
    public void setProfilePictureTest() throws Exception {
        var userLoginResponse = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        userInfoControllerQueries.setProfilePicture(
                        PENGUIN_RESOURCES_PICTURE_PATH,
                        userLoginResponse.getAccessToken())
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void receiveFullPictureAndThumbnailTest() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var userLoginResponse = loginPreconfigured(mockMvc, userUsername);
        userInfoControllerQueries.setProfilePicture(
                        PENGUIN_RESOURCES_PICTURE_PATH,
                        userLoginResponse.getAccessToken());
        var fullPictureMvcResult = userInfoControllerQueries.fullPicture(userUsername, userLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn();
        var expectedImageContent = userInfoService.getFullPictureBytes(userUsername).get();
        var fullPictureResultContent = fullPictureMvcResult.getResponse()
                .getContentAsByteArray();
        assertArrayEquals(expectedImageContent, fullPictureResultContent);
        var thumbnailMvcResult = userInfoControllerQueries.thumbnail(userUsername, userLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn();
        var thumbnailResultContent = thumbnailMvcResult.getResponse()
                .getContentAsByteArray();
        expectedImageContent = ImageConverter.resizeImage(
                expectedImageContent,
                UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                UserInfo.PICTURE_THUMBNAIL_SIZE_PX,
                "png");
        assertArrayEquals(expectedImageContent, thumbnailResultContent);
    }

    public static UserProfileInfo jsonToUserProfileInfo(String usernamesListJson) throws Exception {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.readValue(usernamesListJson, UserProfileInfo.class);
    }
}


