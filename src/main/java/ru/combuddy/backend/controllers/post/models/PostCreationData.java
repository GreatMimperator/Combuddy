package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder({"title", "body", "state", "tags", "postContacts", "postUserContacts"})
public class PostCreationData {

    private String title;
    private String body;
    private Post.State state;
    private List<String> tagNames;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Optional<List<BaseContactInfo>> postContacts;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Optional<List<BaseContactInfo>> postUserContacts;

    @JsonCreator
    public PostCreationData(@JsonProperty("title") String title,
                            @JsonProperty("body") String body,
                            @JsonProperty("state") Post.State state,
                            @JsonProperty("tagNames") List<String> tagNames,
                            @JsonProperty("postContacts") Optional<List<BaseContactInfo>> postContacts,
                            @JsonProperty("postUserContacts") Optional<List<BaseContactInfo>> postUserContacts) {
        this.title = title;
        this.body = body;
        this.state = state;
        this.tagNames = tagNames;
        this.postContacts = postContacts;
        this.postUserContacts = postUserContacts;
    }
}
