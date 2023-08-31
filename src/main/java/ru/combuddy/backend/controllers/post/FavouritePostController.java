package ru.combuddy.backend.controllers.post;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.post.service.interfaces.FavouritePostService;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/post/favourite")
@AllArgsConstructor
public class FavouritePostController {

    private final FavouritePostService favouritePostService;

    @PutMapping("/add/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addPostToFavourites(@PathVariable Long postId, Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        favouritePostService.addPostToFavourites(postId, receiverUsername);
    } // todo: test add and remove after receiving posts by pages

    @DeleteMapping("/delete/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostFromFavourites(@PathVariable Long postId, Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        favouritePostService.deletePostFromFavourites(postId, receiverUsername);
    }
}
