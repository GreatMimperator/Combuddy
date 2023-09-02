package ru.combuddy.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.collections.Pair;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.combuddy.backend.controllers.user.models.LoginResponse;
import ru.combuddy.backend.queries.user.AuthControllerQueries;
import ru.combuddy.backend.security.RoleName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerTest {

    public final static Map<String, String> userPasswords;

    public final static String RANDOM_USER_USERNAME = "random_user";
    public final static String MODERATOR_USERNAME = "moderator";
    public final static String MAIN_MODERATOR_USERNAME = "main_moderator";
    public final static String ANOTHER_MODERATOR_USERNAME = "another_moderator";

    static {
        userPasswords = new HashMap<>();
        userPasswords.put(RANDOM_USER_USERNAME, "random_user_password");
        userPasswords.put(MODERATOR_USERNAME, "moderator_password");
        userPasswords.put(MAIN_MODERATOR_USERNAME, "main_moderator_password");
        userPasswords.put(ANOTHER_MODERATOR_USERNAME, "another_moderator_password");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${jwt.accessToken.expiresInSeconds}")
    private Long accessTokenExpiresInSeconds;
    @Value("${jwt.refreshToken.expiresInSeconds}")
    private Long refreshTokenExpiresInSeconds;

    @Autowired
    private AuthControllerQueries authControllerQueries;

    @Test
    public void loginTest() throws Exception {
        var username = MODERATOR_USERNAME;
        var password = userPasswords.get(username);
        var loginResponseJson = authControllerQueries.login(username, password)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var loginResponse = jsonToLoginResponse(loginResponseJson);
        assertLoginResponse(loginResponse, username, RoleName.ROLE_MODERATOR);
    }

    @Test
    public void registerTest() throws Exception {
        var username = "test_created_user";
        var password = username + "_password";
        var loginResponseJson = authControllerQueries.register(username, password)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var loginResponse = jsonToLoginResponse(loginResponseJson);
        assertLoginResponse(loginResponse, username, RoleName.ROLE_USER);
        loginResponseJson = authControllerQueries.login(username, password)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        loginResponse = jsonToLoginResponse(loginResponseJson);
        assertLoginResponse(loginResponse, username, RoleName.ROLE_USER);
    }

    @Test
    public void refreshTokenTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var userLoginResponse = loginPreconfiguredReceiveResponse(mockMvc, username);
        var loginResponseJson = authControllerQueries.refreshToken(userLoginResponse.getRefreshToken())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var loginResponse = jsonToLoginResponse(loginResponseJson);
        assertLoginResponse(loginResponse, username, RoleName.ROLE_USER);
    }

    @Test
    public void logoutTest() throws Exception {
        var loginResponse = loginPreconfiguredReceiveResponse(mockMvc, RANDOM_USER_USERNAME);
        authControllerQueries.logout(loginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        authControllerQueries.refreshToken(loginResponse.getRefreshToken())
                .andExpect(status().isNotFound());
    }


    private void assertLoginResponse(LoginResponse loginResponse, String username, RoleName roleName) {
        var accessToken = jwtDecoder.decode(loginResponse.getAccessToken());
        assert accessToken.getSubject().equals(username);
        assert accessToken.getClaim("scope").equals(roleName.name());
        assert accessToken.getIssuedAt() != null;
        assert Duration.between(accessToken.getIssuedAt(), accessToken.getExpiresAt())
                .equals(Duration.ofSeconds(accessTokenExpiresInSeconds));
        var refreshToken = jwtDecoder.decode(loginResponse.getRefreshToken());
        assert refreshToken.getSubject().equals(username);
        assert refreshToken.getIssuedAt() != null;
        assert Duration.between(refreshToken.getIssuedAt(), refreshToken.getExpiresAt())
                .equals(Duration.ofSeconds(refreshTokenExpiresInSeconds));
    }


    /**
     * @return accept token
     */
    public static String loginPreconfigured(MockMvc mockMvc, String username) throws Exception {
        return loginPreconfiguredReceiveResponse(mockMvc, username).getAccessToken();
    }

    public static LoginResponse loginPreconfiguredReceiveResponse(MockMvc mockMvc, String username)
            throws Exception {
        var password = userPasswords.get(username);
        assert password != null;
        return loginReceiveResponse(mockMvc, username, password);
    }

    /**
     * @return accept token
     */
    public static String login(MockMvc mockMvc, String username, String password) throws Exception {
        return loginReceiveResponse(mockMvc, username, password).getAccessToken();
    }

    public static LoginResponse loginReceiveResponse(MockMvc mockMvc, String username, String password) throws Exception {
        var loginResponseJson = new AuthControllerQueries(mockMvc).login(username, password)
                .andReturn().getResponse().getContentAsString();
        return jsonToLoginResponse(loginResponseJson);
    }

    public static LoginResponse jsonToLoginResponse(String loginResponseJson) throws Exception {
        return new ObjectMapper().readValue(loginResponseJson, LoginResponse.class);
    }

    public static MockHttpServletRequestBuilder bearer(MockHttpServletRequestBuilder builder, String bearerToken) {
        return builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
    }

    public static Pair<String, String> preconfiguredCredentials(String username) {
        var password = userPasswords.get(username);
        assert password != null;
        return Pair.create(username, password);
    }
}