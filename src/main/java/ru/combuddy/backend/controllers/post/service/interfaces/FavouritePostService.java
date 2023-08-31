package ru.combuddy.backend.controllers.post.service.interfaces;

import ru.combuddy.backend.exceptions.post.InvalidPostIdException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

public interface FavouritePostService {
    void addPostToFavourites(Long postId, String subscriberUsername)
            throws UserNotExistsException,
            InvalidPostIdException;

    boolean exists(Long postId, Long subscriberId);

    boolean deletePostFromFavourites(Long postId, String subscriberUsername);
}
