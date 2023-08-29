package ru.combuddy.backend.repositories.post;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.post.FavoritePost;

public interface FavoritePostRepository extends CrudRepository<FavoritePost, Long> {
    boolean existsByPostIdAndSubscriberId(Long postId, Long subscriberId);
    int deleteByPostIdAndSubscriberUsername(Long postId, String subscriberUsername);
}
