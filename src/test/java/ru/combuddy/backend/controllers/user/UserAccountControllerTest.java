package ru.combuddy.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.graalvm.collections.Pair;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.queries.user.AuthControllerQueries;
import ru.combuddy.backend.queries.user.UserAccountControllerQueries;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;
import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MODERATOR;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthControllerQueries authControllerQueries;

    @Autowired
    private UserAccountControllerQueries userAccountControllerQueries;

    @Test
    public void freezeUnfreezeUserByModeratorTest() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var moderatorCredentials = preconfiguredCredentials(MODERATOR_USERNAME);
        receiveFreezeResultActions(mockMvc, moderatorCredentials, userUsername)
                .andExpect(status().isNoContent());
        var isFrozen = userAccountService.findByUsername(userUsername).get().getFrozen();
        assert isFrozen;
        receiveUnfreezeResultActions(mockMvc, moderatorCredentials, userUsername)
                .andExpect(status().isNoContent());
        isFrozen = userAccountService.findByUsername(userUsername).get().getFrozen();
        assert !isFrozen;
    }

    @Test
    public void deleteUserByMainModeratorTest() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var mainModeratorLoginResponse = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        userAccountControllerQueries.delete(userUsername, mainModeratorLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        authControllerQueries.register(userUsername, "anything")
                .andExpect(status().isCreated());
    }

    @Test
    public void usernamesBeginWithTest() throws Exception {
        var userLoginResponse = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        var usernamesListJson = userAccountControllerQueries.usernamesBeginWith("m", userLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernames").isArray())
                .andReturn().getResponse().getContentAsString();
        var usernamesList = jsonToUsernamesList(usernamesListJson);
        var mockUsernamesList = List.of(MODERATOR_USERNAME, MAIN_MODERATOR_USERNAME);
        // equals check
        assert usernamesList.getUsernames().containsAll(mockUsernamesList) &&
                mockUsernamesList.containsAll(usernamesList.getUsernames());
    }

    @Test
    @Transactional
    public void setModeratorToUserByMainModerator() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var mainModeratorLoginResponse = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        userAccountControllerQueries.roleSet(ROLE_MODERATOR.name(),
                        userUsername,
                        mainModeratorLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        assert userAccountService.findByUsername(userUsername).get().getRole().getName() == ROLE_MODERATOR;
    }

    public static UsernamesList jsonToUsernamesList(String usernamesListJson) throws Exception {
        return new ObjectMapper().readValue(usernamesListJson, UsernamesList.class);
    }


    public static boolean freezePreconfigured(MockMvc mockMvc, String freezerUsername, String toBeFrozenUsername) throws Exception {
        return freeze(mockMvc, Pair.create(freezerUsername, userPasswords.get(freezerUsername)), toBeFrozenUsername);
    }

    public static boolean unfreezePreconfigured(MockMvc mockMvc, String unfreezerUsername, String toBeUnfrozenUsername) throws Exception {
        return unfreeze(mockMvc, Pair.create(unfreezerUsername, userPasswords.get(unfreezerUsername)), toBeUnfrozenUsername);
    }

    public static boolean freeze(MockMvc mockMvc, Pair<String, String> freezerCredentials, String toBeFrozenUsername) throws Exception {
        var responseCode = receiveFreezeResultActions(mockMvc, freezerCredentials, toBeFrozenUsername)
                .andReturn().getResponse();
        return responseCode.getStatus() == MockHttpServletResponse.SC_NO_CONTENT;
    }

    public static boolean unfreeze(MockMvc mockMvc, Pair<String, String> unfreezerCredentials, String toBeUnfrozenUsername) throws Exception {
        var responseCode = receiveUnfreezeResultActions(mockMvc, unfreezerCredentials, toBeUnfrozenUsername)
                .andReturn().getResponse();
        return responseCode.getStatus() == MockHttpServletResponse.SC_NO_CONTENT;
    }


    public static ResultActions receiveFreezeResultActions(MockMvc mockMvc, Pair<String, String> freezerCredentials, String toBeFrozenUsername) throws Exception {
        var freezerUsername = freezerCredentials.getLeft();
        var freezerPassword = freezerCredentials.getRight();
        var freezerLoginResponse = login(mockMvc, freezerUsername, freezerPassword);
        return new UserAccountControllerQueries(mockMvc).freeze(toBeFrozenUsername, freezerLoginResponse.getAccessToken());
    }

    public static ResultActions receiveUnfreezeResultActions(MockMvc mockMvc, Pair<String, String> unfreezerCredentials, String toBeUnfrozenUsername) throws Exception {
        var unfreezerUsername = unfreezerCredentials.getLeft();
        var unfreezerPassword = unfreezerCredentials.getRight();
        var unfreezerLoginResponse = login(mockMvc, unfreezerUsername, unfreezerPassword);
        return new UserAccountControllerQueries(mockMvc).unfreeze(toBeUnfrozenUsername, unfreezerLoginResponse.getAccessToken());
    }

}
