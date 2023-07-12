package ru.combuddy.backend.controllers.post;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody PostCreationData postCreationData) {
        var created = postService.create(postCreationData);
        if (!created) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
    }

    @PatchMapping(value = "/archive/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long postId) {
        var archived = postService.updateArchivedState(postId, true);
        if (!archived) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Posts with this id do not exist");
        }
    }

    @PatchMapping(value = "/unarchive/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchive(@PathVariable Long postId) {
        var archived = postService.updateArchivedState(postId, false);
        if (!archived) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Posts with this id do not exist");
        }
    }
}
