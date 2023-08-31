package ru.combuddy.backend.queries.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class SubscriptionsControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions subscribe(String posterUsername, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/user/subscribe/{posterUsername}", posterUsername),
                subscriberAccessToken));
    }

    public ResultActions unsubscribe(String posterUsername, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/v1/user/unsubscribe/{posterUsername}", posterUsername),
                subscriberAccessToken));
    }

    public ResultActions subscriptions(String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/subscriptions/usernames"),
                subscriberAccessToken));
    }

    public ResultActions subscriptionsBeginWith(String beginWith, String subscriberAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/subscriptions/usernames/beginWith/{beginWith}", beginWith),
                subscriberAccessToken));
    }

    public ResultActions subscribers(String posterAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/subscribers/usernames"),
                posterAccessToken));
    }

    public ResultActions subscribersBeginWith(String beginWith, String posterAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/subscribers/usernames/beginWith/{beginWith}", beginWith),
                posterAccessToken));
    }
}
