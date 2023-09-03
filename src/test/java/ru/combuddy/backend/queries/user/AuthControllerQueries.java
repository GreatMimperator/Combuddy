package ru.combuddy.backend.queries.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class AuthControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions login(String username, String password) throws Exception {
        return this.mockMvc.perform(post("/api/v1/user/auth/login/{username}", username)
                .header("password", password));
    }

    public ResultActions register(String username, String password) throws Exception {
        return this.mockMvc.perform(post("/api/v1/user/auth/register/{username}", username)
                .header("password", password));
    }

    public ResultActions refreshToken(String refreshToken) throws Exception {
        return this.mockMvc.perform(bearer(post("/api/v1/user/auth/refresh-token"), refreshToken));
    }

    public ResultActions logout(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(post("/api/v1/user/auth/logout"), accessToken));
    }
}
