package ru.combuddy.backend.queries.contact;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class UserContactControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions add(String contactTypeAsString, String contact, String ownerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/contact/put")
                        .queryParam("contactType", contactTypeAsString)
                        .queryParam("contact", contact),
                ownerAccessToken));
    }

    public ResultActions delete(String contactTypeAsString, String contact, String ownerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                MockMvcRequestBuilders.delete("/api/v1/contact/delete")
                        .queryParam("contactType", contactTypeAsString)
                        .queryParam("contact", contact),
                ownerAccessToken));
    }
}