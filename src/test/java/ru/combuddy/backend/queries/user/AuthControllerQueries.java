package ru.combuddy.backend.queries.user;

import lombok.NoArgsConstructor;
import org.graalvm.collections.Pair;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class AuthControllerQueries {
    MockMvc mockMvc;

    public AuthControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions login(Pair<String, String> credentials) throws Exception {
        return login(credentials.getLeft(), credentials.getRight());
    }

    public ResultActions login(String username, String password) throws Exception {
        return this.mockMvc.perform(post("/api/user/auth/login/{username}", username)
                .queryParam("password", password));
    }

    public ResultActions register(String username, String password) throws Exception {
        return this.mockMvc.perform(post("/api/user/auth/register/{username}", username)
                .queryParam("password", password));
    }

    public ResultActions refreshToken(String refreshToken) throws Exception {
        return this.mockMvc.perform(bearer(post("/api/user/auth/refresh-token"), refreshToken));
    }

    public ResultActions logout(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(post("/api/user/auth/logout"), accessToken));
    }
}
