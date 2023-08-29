package ru.combuddy.backend.queries.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.user.models.UserProfileInfo;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class PostControllerQueries {
    MockMvc mockMvc;

    public PostControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions create(PostCreationData postCreationData, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                post("/api/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreationDataToJson(postCreationData)),
                accessToken));
    }

    public ResultActions remove(Long postId, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/post/remove/{postId}", postId),
                accessToken));
    }

    public ResultActions updateTitle(Long postId, String newTitle, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/title/{newTitle}", postId, newTitle),
                accessToken));
    }

    public ResultActions updateBody(Long postId, String newBody, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/body/{newBody}", postId, newBody),
                accessToken));
    }

    public ResultActions updateState(Long postId, String newState, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/state/{newState}", postId, newState),
                accessToken));
    }

    public ResultActions updateTags(Long postId, List<String> tagNames, String accessToken) throws Exception {
        var commaSeparatedTagNames = String.join(",", tagNames);
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/tags/{commaSeparatedTagNames}",
                        postId,
                        commaSeparatedTagNames),
                accessToken));
    }

    public ResultActions updatePostContacts(Long postId, ContactList postContacts, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/post-contacts", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactListToJson(postContacts)),
                accessToken));
    }

    public ResultActions updateUserContacts(Long postId, ContactList postUserContacts, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/update/{postId}/user-contacts", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactListToJson(postUserContacts)),
                accessToken));
    }

    public ResultActions info(Long postId, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/post/info/{postId}", postId),
                accessToken));
    }

    public static String postCreationDataToJson(PostCreationData postCreationData) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.writeValueAsString(postCreationData);
    }

    public static String contactListToJson(ContactList contactList) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.writeValueAsString(contactList);
    }
}
