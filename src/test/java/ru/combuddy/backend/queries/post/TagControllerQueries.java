package ru.combuddy.backend.queries.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.bearer;

@Service
@AllArgsConstructor
public class TagControllerQueries {

    private final MockMvc mockMvc;

    public ResultActions add(String name, String description, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                put("/api/v1/post/tag/add/{name}", name)
                        .queryParam("description", description), // todo: maybe make them constants?
                accessToken));
    }

    public ResultActions delete(String name, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                MockMvcRequestBuilders.delete("/api/v1/post/tag/delete/{name}", name),
                accessToken));
    }

    public ResultActions changeDescription(String name, String description, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                patch("/api/v1/post/tag/change/description/{name}", name)
                        .queryParam("description", description),
                accessToken));
    }

    public ResultActions namesBeginWith(String nameBeginPart, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/post/tag/names/beginWith/{nameBeginPart}", nameBeginPart),
                accessToken));
    }

    public ResultActions namesAll(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/post/tag/names/all"),
                accessToken));
    }

    public ResultActions description(String name, String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/post/tag/description/{name}", name),
                accessToken));
    }

    public ResultActions homeTagUpdate(List<String> includedHomeTagNames,
                                       List<String> excludedHomeTagNames,
                                       String userAccessToken) throws Exception {
        var commaSeparatedIncludedHomeTagNames = String.join(",", includedHomeTagNames);
        var commaSeparatedExcludedHomeTagNames = String.join(",", excludedHomeTagNames);
        return this.mockMvc.perform(bearer(
                put("/api/v1/post/tag/home/set")
                        .queryParam("commaSeparatedIncludedTags", commaSeparatedIncludedHomeTagNames)
                        .queryParam("commaSeparatedExcludedTags", commaSeparatedExcludedHomeTagNames),
                userAccessToken));
    }

    public ResultActions homeTagGet(String accessToken) throws Exception {
        return this.mockMvc.perform(bearer(
                get("/api/v1/post/tag/home/get"),
                accessToken));
    }
}
