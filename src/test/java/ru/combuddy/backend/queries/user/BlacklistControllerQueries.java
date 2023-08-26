package ru.combuddy.backend.queries.user;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class BlacklistControllerQueries {

    MockMvc mockMvc;

    public BlacklistControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions add(String userToAdd, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/user/blacklist/add/{userToAdd}", userToAdd),
                askerAccessToken));
    }

    public ResultActions remove(String userToRemove, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/user/blacklist/remove/{userToRemove}", userToRemove),
                askerAccessToken));
    }

    public ResultActions aggressors(String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/user/blacklist/aggressors"),
                askerAccessToken));
    }

}
