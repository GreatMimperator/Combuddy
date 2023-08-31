package ru.combuddy.backend.queries.user;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class UserInfoControllerQueries {

    private final MockMvc mockMvc;
    private final ResourceLoader resourceLoader;

    public ResultActions all(String receiveUsername, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/info/all/{receiveUsername}", receiveUsername),
                askerAccessToken));
    }

    public ResultActions setProfilePicture(String resourcesRelatedPath,
                                           String senderAccessToken)
            throws Exception, IOException {
        var pictureResource = this.resourceLoader.getResource("classpath:" + resourcesRelatedPath);
        assert pictureResource.exists();
        var pictureStream = pictureResource.getInputStream();
        var picture = new MockMultipartFile("picture", pictureStream);
        return this.mockMvc.perform(bearer(
                multipart("/api/v1/user/info/set-profile-picture")
                        .file(picture),
                senderAccessToken));
    }

    public ResultActions fullPicture(String username, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/info/full-picture/{username}", username),
                askerAccessToken));

    }

    public ResultActions thumbnail(String username, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/info/thumbnail/{username}", username),
                askerAccessToken));
    }
}
