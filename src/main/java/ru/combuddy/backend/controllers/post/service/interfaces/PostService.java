package ru.combuddy.backend.controllers.post.service.interfaces;

import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.NotExistsException;

public interface PostService {
    /**
     * @throws NotExistsException If creater account with this username doesn't exist
     */
    void create(Post post, String creatorUsername) throws NotExistsException;

    /**
     * @throws NotExistsException If post with this id doesn't exist
     */
    void updateArchivedState(Long postId, String archivistUsername, boolean archived) throws NotExistsException;
}
