package ru.combuddy.backend.queries.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class FavouritePostControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions add(Long postId, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/post/favourite/add/{postId}", postId),
                accessToken));
    }

    public ResultActions delete(Long postId, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                MockMvcRequestBuilders.delete("/api/v1/post/favourite/delete/{postId}", postId),
                accessToken));
    }
}
