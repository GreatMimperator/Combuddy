package ru.combuddy.backend.controllers.post;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.NotExistsException;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Post post, Authentication authentication) {
        var creatorUsername = authentication.getName();
        try {
            postService.create(post, creatorUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @PatchMapping(value = "/archive/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long postId, Authentication authentication) {
        setArchivedState(postId, authentication, true);
    }



    @PatchMapping(value = "/unarchive/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchive(@PathVariable Long postId, Authentication authentication) {
        setArchivedState(postId, authentication, false);
    }

    private void setArchivedState(Long postId, Authentication authentication, boolean archived) {
        try {
            postService.updateArchivedState(postId, authentication.getName(), archived);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Posts with this id do not exist");
        }
    }
}
