package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;

@Data
@JsonPropertyOrder({"posterUsername", "post"})
public class PostCreationData {

    @NotNull
    @Size(min = UserAccount.MIN_USERNAME_LENGTH,
            max = UserAccount.MAX_USERNAME_LENGTH)
    @JsonProperty("poster_username")
    private String posterUsername;

    @Valid
    private Post post;
}
