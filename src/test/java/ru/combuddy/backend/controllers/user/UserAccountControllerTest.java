package ru.combuddy.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
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
import ru.combuddy.backend.controllers.ServiceConstants;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.queries.user.AuthControllerQueries;
import ru.combuddy.backend.queries.user.UserAccountControllerQueries;

import java.util.LinkedList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.Util.listEqualsIgnoreOrder;
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

    @Autowired
    private ServiceConstants serviceConstants;


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
        var mainModeratorAccessToken = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        userAccountControllerQueries.delete(userUsername, mainModeratorAccessToken)
                .andExpect(status().isNoContent());
        authControllerQueries.register(userUsername, "anything")
                .andExpect(status().isCreated());
    }

    @Test
    public void usernamesBeginWithTest() throws Exception {
        var accessToken = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        var answerPageSize = serviceConstants.getUsersBeginWithPerPage();
        var n = 3;
        var kaStartedUsernamesNPagesSizeList = new LinkedList<String>();
        for (int pageIndex = 0; pageIndex < n; pageIndex++) {
            for (int i = 0; i < answerPageSize; i++) {
                String usernameSecondPart;
                do {
                    usernameSecondPart = RandomStringUtils.randomAlphabetic(5, 10);
                } while(kaStartedUsernamesNPagesSizeList.contains(usernameSecondPart));
                var username = "ka" + usernameSecondPart;
                kaStartedUsernamesNPagesSizeList.add(username);
                userAccountService.createDefaultUser(username);
            }
        }
        var gotUsernamesList = new LinkedList<String>();
        for (int pageIndex = 0; pageIndex < n; pageIndex++) {
            var usernamesListJson = userAccountControllerQueries.usernamesBeginWith("ka",
                            pageIndex + 1,
                            accessToken)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            gotUsernamesList.addAll(jsonToUsernamesList(usernamesListJson).getUsernames());
        }
        assert listEqualsIgnoreOrder(gotUsernamesList, kaStartedUsernamesNPagesSizeList);
    }

    @Test
    @Transactional
    public void setModeratorToUserByMainModerator() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var mainModeratorAccessToken = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        userAccountControllerQueries.roleSet(ROLE_MODERATOR.name(),
                        userUsername,
                        mainModeratorAccessToken)
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
        var freezerAccessToken = login(mockMvc, freezerUsername, freezerPassword);
        return new UserAccountControllerQueries(mockMvc).freeze(toBeFrozenUsername, freezerAccessToken);
    }

    public static ResultActions receiveUnfreezeResultActions(MockMvc mockMvc, Pair<String, String> unfreezerCredentials, String toBeUnfrozenUsername) throws Exception {
        var unfreezerUsername = unfreezerCredentials.getLeft();
        var unfreezerPassword = unfreezerCredentials.getRight();
        var unfreezerAccessToken = login(mockMvc, unfreezerUsername, unfreezerPassword);
        return new UserAccountControllerQueries(mockMvc).unfreeze(toBeUnfrozenUsername, unfreezerAccessToken);
    }

}
