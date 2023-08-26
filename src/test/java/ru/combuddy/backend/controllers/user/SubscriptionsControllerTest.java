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
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.queries.user.SubscriptionsControllerQueries;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;
import static ru.combuddy.backend.controllers.user.UserAccountControllerTest.jsonToUsernamesList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubscriptionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionsControllerQueries subscriptionsControllerQueries;

    @Test
    @Transactional
    public void subscribeUnsubscribeTest() throws Exception {
        var subscriberUsername = MODERATOR_USERNAME;
        var posterUsername = MAIN_MODERATOR_USERNAME;
        var subscriberLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var subscription = subscriptionService.findSubscription(posterUsername, subscriberUsername);
        assert subscription.isEmpty();
        // subscribe
        subscriptionsControllerQueries.subscribe(posterUsername, subscriberLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        subscription = subscriptionService.findSubscription(posterUsername, subscriberUsername);
        assert subscription.isPresent();
        // unsubscribe
        subscriptionsControllerQueries.unsubscribe(posterUsername, subscriberLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        subscription = subscriptionService.findSubscription(posterUsername, subscriberUsername);
        assert subscription.isEmpty();
    }

    @Test
    public void subscriptionsTest() throws Exception {
        var loginResponse = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        var userSubscriptionsJson = subscriptionsControllerQueries.subscriptions(loginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var userSubscriptions = jsonToUsernamesList(userSubscriptionsJson).getUsernames();
        var expectedSubscriptions = List.of(MODERATOR_USERNAME, ANOTHER_MODERATOR_USERNAME);
        assert userSubscriptions.containsAll(expectedSubscriptions) &&
                expectedSubscriptions.containsAll(userSubscriptions);
        var userSubscriptionsBeginWithMJson = subscriptionsControllerQueries.subscriptionsBeginWith(
                "m",
                        loginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var userSubscriptionsBeginWithM = jsonToUsernamesList(userSubscriptionsBeginWithMJson).getUsernames();
        expectedSubscriptions = List.of(MODERATOR_USERNAME);
        assert userSubscriptionsBeginWithM.containsAll(expectedSubscriptions) &&
                expectedSubscriptions.containsAll(userSubscriptionsBeginWithM);
    }

    @Test
    public void subscribersTest() throws Exception {
        var loginResponse = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        var userSubscribersJson = subscriptionsControllerQueries.subscribers(loginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var userSubscribers = jsonToUsernamesList(userSubscribersJson).getUsernames();
        var expectedSubscribers = List.of(MODERATOR_USERNAME, MAIN_MODERATOR_USERNAME, ANOTHER_MODERATOR_USERNAME);
        assert userSubscribers.containsAll(expectedSubscribers) &&
                expectedSubscribers.containsAll(userSubscribers);
        var userSubscribersBeginWithMJson = subscriptionsControllerQueries.subscribersBeginWith(
                        "m",
                        loginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var userSubscribersBeginWithM = jsonToUsernamesList(userSubscribersBeginWithMJson).getUsernames();
        expectedSubscribers = List.of(MODERATOR_USERNAME, MAIN_MODERATOR_USERNAME);
        assert userSubscribersBeginWithM.containsAll(expectedSubscribers) &&
                expectedSubscribers.containsAll(userSubscribersBeginWithM);
    }
}
