package ru.combuddy.backend.queries.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class BlacklistControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions add(String userToAdd, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/user/blacklist/add/{userToAdd}", userToAdd),
                askerAccessToken));
    }

    public ResultActions delete(String userToRemove, String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                MockMvcRequestBuilders.delete("/api/v1/user/blacklist/delete/{userToRemove}", userToRemove),
                askerAccessToken));
    }

    public ResultActions aggressors(String askerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/blacklist/aggressors"),
                askerAccessToken));
    }

}
