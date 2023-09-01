package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@JsonPropertyOrder({"title", "body", "state", "tags", "postContacts", "userContacts"})
public class PostCreationData {
    @NotNull
    private String title;
    @NotNull
    private String body;
    @NotNull
    private Post.State state;
    @NotNull
    private List<String> tags;
    private List<BaseContactInfo> postContacts;
    private List<BaseContactInfo> userContacts;
}
