package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder({"title", "body", "state", "tags", "postContacts", "userContacts"})
public class PostCreationData {

    private String title;
    private String body;
    private Post.State state;
    private List<String> tags;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Optional<List<BaseContactInfo>> postContacts;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Optional<List<BaseContactInfo>> userContacts;

    @JsonCreator
    public PostCreationData(@JsonProperty("title") String title,
                            @JsonProperty("body") String body,
                            @JsonProperty("state") Post.State state,
                            @JsonProperty("tags") List<String> tags,
                            @JsonProperty("postContacts") Optional<List<BaseContactInfo>> postContacts,
                            @JsonProperty("userContacts") Optional<List<BaseContactInfo>> userContacts) {
        this.title = title;
        this.body = body;
        this.state = state;
        this.tags = tags;
        this.postContacts = postContacts;
        this.userContacts = userContacts;
    }
}
