package ru.combuddy.backend.controllers.post.service.interfaces;

import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.entities.post.Post;

public interface PostService {
    /**
     * @return false if data hasn't user with this username, true if created
     */
    boolean create(PostCreationData postCreationData);

    /**
     * @return false if post with id doesn't exist, true if set
     */
    boolean updateArchivedState(Long postId, boolean archived);
}
