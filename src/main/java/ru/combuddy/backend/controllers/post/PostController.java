package ru.combuddy.backend.controllers.post;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.post.models.IdsList;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.converters.CommaSepListConverter;
import ru.combuddy.backend.entities.post.Post;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    private CommaSepListConverter commaSepListConverter;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody PostCreationData postCreationData, Authentication authentication) {
        var creatorUsername = getUsername(authentication);
        var post = postService.create(postCreationData, creatorUsername);
        return post.getId();
    }

    @DeleteMapping("/delete/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long postId, Authentication authentication) {
        var deleterUsername = getUsername(authentication);
        postService.delete(postId, deleterUsername);
    }

    @PatchMapping("/update/{postId}/title/{newTitle}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTitle(@PathVariable Long postId,
                            @PathVariable String newTitle,
                            Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        postService.updateTitle(postId, updaterUsername, newTitle);
    }

    @PatchMapping("/update/{postId}/body/{newBody}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBody(@PathVariable Long postId,
                            @PathVariable String newBody,
                            Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        postService.updateBody(postId, updaterUsername, newBody);
    }

    @PatchMapping("/update/{postId}/state/{newStateAsString}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateState(@PathVariable Long postId,
                            @PathVariable String newStateAsString,
                            Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        var newState = Post.State.convertToState(newStateAsString);
        postService.updateState(postId, updaterUsername, newState);
    }

    @PatchMapping("/update/{postId}/tags/{commaSeparatedTags}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTags(@PathVariable Long postId,
                           @PathVariable String commaSeparatedTags,
                           Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        var tagNames = commaSepListConverter.convert(commaSeparatedTags);
        postService.updateTags(postId, updaterUsername, tagNames);
    }

    @PatchMapping("/update/{postId}/post-contacts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePostContacts(@PathVariable Long postId,
                                   @RequestBody ContactList contactList,
                                   Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        postService.updatePostContacts(postId, updaterUsername, contactList);
    }

    @PatchMapping("/update/{postId}/user-contacts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePostUserContacts(@PathVariable Long postId,
                                       @RequestBody ContactList contactList,
                                       Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        postService.updatePostUserContacts(postId, updaterUsername, contactList);
    }

    @GetMapping("/info/{postId}")
    public PostInfo getPostInfo(@PathVariable Long postId, Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        return postService.getPostInfo(postId, receiverUsername);
    }

    @GetMapping("/all/page/{pageNumber}")
    public IdsList getAll(@PathVariable int pageNumber,
                          @RequestParam(defaultValue = "posted") String commaSeparatedStates,
                          Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        var stateNames = commaSepListConverter.convert(commaSeparatedStates);
        return new IdsList(postService.searchMain(pageNumber, stateNames, receiverUsername));
    }

    @GetMapping("/home/page/{pageNumber}")
    public IdsList getHome(@PathVariable int pageNumber,
                          @RequestParam(defaultValue = "posted") String commaSeparatedStates,
                          Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        var stateNames = commaSepListConverter.convert(commaSeparatedStates);
        return new IdsList(postService.searchHome(pageNumber, stateNames, receiverUsername));
    }

    @GetMapping("/subscriptions/page/{pageNumber}")
    public IdsList getSubscriptions(@PathVariable int pageNumber,
                           @RequestParam(defaultValue = "posted") String commaSeparatedStates,
                           Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        var stateNames = commaSepListConverter.convert(commaSeparatedStates);
        return new IdsList(postService.getSubscriptionsFeed(pageNumber, stateNames, receiverUsername));
    }

    @GetMapping("/favourites/page/{pageNumber}")
    public IdsList getFavourites(@PathVariable int pageNumber,
                                 @RequestParam(defaultValue = "posted") String commaSeparatedStates,
                                 Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        var stateNames = commaSepListConverter.convert(commaSeparatedStates);
        return new IdsList(postService.searchFavourites(pageNumber, stateNames, receiverUsername));
    }
}
