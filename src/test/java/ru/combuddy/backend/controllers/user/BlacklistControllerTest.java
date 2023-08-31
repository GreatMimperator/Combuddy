package ru.combuddy.backend.controllers.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.queries.user.BlacklistControllerQueries;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.Util.listEqualsIgnoreOrder;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;
import static ru.combuddy.backend.controllers.user.UserAccountControllerTest.jsonToUsernamesList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlacklistControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private BlacklistControllerQueries blacklistControllerQueries;

    @Test
    @Transactional
    public void addRemoveTest() throws Exception {
        var defendedUsername = RANDOM_USER_USERNAME;
        var defendedAccessToken = loginPreconfigured(mockMvc, defendedUsername);
        var aggressorUsername = MODERATOR_USERNAME;
        // adding
        blacklistControllerQueries.add(aggressorUsername, defendedAccessToken)
                .andExpect(status().isNoContent());
        var blacklistRecord = blackListService.findRecord(aggressorUsername, defendedUsername);
        assert blacklistRecord.isPresent();
        // removing
        blacklistControllerQueries.delete(aggressorUsername, defendedAccessToken)
                .andExpect(status().isNoContent());
        blacklistRecord = blackListService.findRecord(aggressorUsername, defendedUsername);
        assert blacklistRecord.isEmpty();
    }

    @Test
    public void aggressorsTest() throws Exception {
        var defendedAccessToken = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        // adding
        var aggressorUsername = MODERATOR_USERNAME;
        blacklistControllerQueries.add(aggressorUsername, defendedAccessToken)
                .andExpect(status().isNoContent());
        aggressorUsername = MAIN_MODERATOR_USERNAME;
        blacklistControllerQueries.add(aggressorUsername, defendedAccessToken)
                .andExpect(status().isNoContent());
        // receiving
        var aggressorsJson = blacklistControllerQueries.aggressors(defendedAccessToken)
                .andReturn().getResponse().getContentAsString();
        var aggressors = jsonToUsernamesList(aggressorsJson).getUsernames();
        var expectedAggressors = List.of(MODERATOR_USERNAME, MAIN_MODERATOR_USERNAME);
        assert listEqualsIgnoreOrder(aggressors, expectedAggressors);
    }
}
