package ru.combuddy.backend.queries.contact;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class UserContactControllerQueries {

    MockMvc mockMvc;

    public UserContactControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions add(String contactTypeAsString, String contact, String ownerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/contact/add")
                        .queryParam("contactType", contactTypeAsString)
                        .queryParam("contact", contact),
                ownerAccessToken));
    }

    public ResultActions remove(String contactTypeAsString, String contact, String ownerAccessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/contact/remove")
                        .queryParam("contactType", contactTypeAsString)
                        .queryParam("contact", contact),
                ownerAccessToken));
    }
}