package ru.combuddy.backend.queries.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.combuddy.backend.controllers.user.models.PrivacyPolicyInfo;

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

    public ResultActions setPrivacyPolicy(PrivacyPolicyInfo privacyPolicyInfo, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/user/info/privacy-policy/set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(privacyPolicyInfoToJson(privacyPolicyInfo)),
                accessToken));
    }

    public ResultActions getPrivacyPolicy(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/info/privacy-policy/get"),
                accessToken));
    }

    public ResultActions setPrivacyPolicyToDefault(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/user/info/privacy-policy/set-default"),
                accessToken));
    }

    public String privacyPolicyInfoToJson(PrivacyPolicyInfo privacyPolicyInfo) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.writeValueAsString(privacyPolicyInfo);
    }
}
