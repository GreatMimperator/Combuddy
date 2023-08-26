package ru.combuddy.backend.queries.user;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class SubscriptionsControllerQueries {
    MockMvc mockMvc;

    public SubscriptionsControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions subscribe(String posterUsername, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/user/subscribe/{toUsername}", posterUsername),
                subscriberAccessToken));
    }

    public ResultActions unsubscribe(String posterUsername, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/user/unsubscribe/{fromUsername}", posterUsername),
                subscriberAccessToken));
    }

    public ResultActions subscriptions(String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/user/subscriptions/usernames"),
                subscriberAccessToken));
    }

    public ResultActions subscriptionsBeginWith(String beginWith, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/user/subscriptions/usernames/beginWith/{beginWith}", beginWith),
                subscriberAccessToken));
    }

    public ResultActions subscribers(String posterAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/user/subscribers/usernames"),
                posterAccessToken));
    }

    public ResultActions subscribersBeginWith(String beginWith, String posterAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/user/subscribers/usernames/beginWith/{beginWith}", beginWith),
                posterAccessToken));
    }
}
