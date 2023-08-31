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
public class UserAccountControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions freeze(String who, String freezerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                post("/api/v1/user/account/freeze/{who}", who),
                freezerAccessToken));
    }

    public ResultActions unfreeze(String who, String unfreezerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                post("/api/v1/user/account/unfreeze/{who}", who),
                unfreezerAccessToken));
    }


    public ResultActions delete(String toBeDeletedUsername, String deleterAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                MockMvcRequestBuilders.delete("/api/v1/user/account/delete/{username}", toBeDeletedUsername),
                deleterAccessToken));
    }

    public ResultActions usernamesBeginWith(String with, int pageNumber, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/user/account/usernamesBeginWith/{with}/page/{pageNumber}", with, pageNumber),
                accessToken));
    }

    public ResultActions roleSet(String newRoleName, String receiverUsername, String issuerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/user/account/role/set/{newRoleName}/to/{receiverUsername}",
                        newRoleName,
                        receiverUsername),
                issuerAccessToken));
    }


}
