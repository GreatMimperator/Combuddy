package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.post.service.interfaces.FavouritePostService;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.post.FavoritePost;
import ru.combuddy.backend.exceptions.post.InvalidPostIdException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.post.FavoritePostRepository;

@Service
@Transactional
@AllArgsConstructor
public class FavouritePostServiceImpl implements FavouritePostService {

    private FavoritePostRepository favoritePostRepository;
    private UserAccountService userAccountService;
    private PostService postService;

    @Override
    public void addPostToFavourites(Long postId, String subscriberUsername)
            throws UserNotExistsException,
            InvalidPostIdException {
        var subscriber = userAccountService.getByUsername(subscriberUsername);
        var post = postService.getById(postId);
        if (!this.exists(postId, subscriber.getId())) {
            favoritePostRepository.save(
                    new FavoritePost(null, post, subscriber));
        }
    }

    @Override
    public boolean exists(Long postId, Long subscriberId) {
        return favoritePostRepository.existsByPostIdAndSubscriberId(postId, subscriberId);
    }

    @Override
    public boolean deletePostFromFavourites(Long postId, String subscriberUsername) {
        var deletedCount = favoritePostRepository.deleteByPostIdAndSubscriberUsername(postId, subscriberUsername);
        return deletedCount > 0;
    }
}
