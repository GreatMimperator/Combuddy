package ru.combuddy.backend.queries.post;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@NoArgsConstructor
public class TagControllerQueries {
    MockMvc mockMvc;

    public TagControllerQueries(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions add(String name, String description, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/post/tag/add/{name}", name)
                        .queryParam("description", description), // todo: maybe make them constants?
                accessToken));
    }

    public ResultActions remove(String name, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                delete("/api/post/tag/remove/{name}", name),
                accessToken));
    }

    public ResultActions changeDescription(String name, String description, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/post/tag/change-description/{name}", name)
                        .queryParam("description", description),
                accessToken));
    }

    public ResultActions namesBeginWith(String nameBeginPart, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/post/tag/names/beginWith/{nameBeginPart}", nameBeginPart),
                accessToken));
    }

    public ResultActions namesAll(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/post/tag/names/all"),
                accessToken));
    }

    public ResultActions description(String name, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/post/tag/description/{name}", name),
                accessToken));
    }

    public ResultActions homeTagUpdate(List<String> includedHomeTagNames,
                                       List<String> excludedHomeTagNames,
                                       String userAccessToken) throws Exception {
        var commaSeparatedIncludedHomeTagNames = String.join(",", includedHomeTagNames);
        var commaSeparatedExcludedHomeTagNames = String.join(",", excludedHomeTagNames);
        return this.mockMvc.perform(bearer(
                put("/api/post/tag/home/set")
                        .queryParam("commaSeparatedIncludedTags", commaSeparatedIncludedHomeTagNames)
                        .queryParam("commaSeparatedExcludedTags", commaSeparatedExcludedHomeTagNames),
                userAccessToken));
    }

    public ResultActions homeTagGet(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/post/tag/home/get"),
                accessToken));
    }
}
