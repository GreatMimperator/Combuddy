package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.post.Post;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder({"id", "ownerUsername", "title", "body",
        "state", "tags", "postContacts", "postUserContacts",
        "creationDate", "modificationDate", "postedDate"})
public class PostInfo {
    private Long id;
    private String ownerUsername;
    private String title;
    private String body;
    private Post.State state;
    private List<String> tags;
    private List<BaseContactInfo> postContacts;
    private List<BaseContactInfo> postUserContacts;
    private Optional<Calendar> creationDate;
    private Optional<Calendar> modificationDate;
    private Optional<Calendar> postedDate;


    @JsonCreator
    public PostInfo(@JsonProperty("id") Long id,
                    @JsonProperty("ownerUsername") String ownerUsername,
                    @JsonProperty("title") String title,
                    @JsonProperty("body") String body,
                    @JsonProperty("state") Post.State state,
                    @JsonProperty("tags") List<String> tags,
                    @JsonProperty("postContacts") List<BaseContactInfo> postContacts,
                    @JsonProperty("postUserContacts") List<BaseContactInfo> postUserContacts,
                    @JsonProperty("creationDate") Optional<Calendar> creationDate,
                    @JsonProperty("modificationDate") Optional<Calendar> modificationDate,
                    @JsonProperty("postedDate") Optional<Calendar> postedDate) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.title = title;
        this.body = body;
        this.state = state;
        this.tags = tags;
        this.postContacts = postContacts;
        this.postUserContacts = postUserContacts;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.postedDate = postedDate;
    }
}
