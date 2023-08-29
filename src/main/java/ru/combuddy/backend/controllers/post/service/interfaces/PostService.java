package ru.combuddy.backend.controllers.post.service.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.entities.post.FavoritePost;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.util.List;

public interface PostService {
    /**
     * Sets postedDate if state is {@link Post.State#POSTED}
     *
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST}<br>
     * 1. If account with this username doesn't exist <br>
     * 2. If post state is not draft or posted <br>
     * 3. If any post tag does not exist <br>
     * 4. If any user contact does not exist
     */
    Post create(PostCreationData post, String ownerUsername) throws ResponseStatusException;

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if account with this username does not exist <br>
     * with {@link HttpStatus#FORBIDDEN} if receiver can not receive this data
     */
    void delete(Long postId, String removerUsername);

    /**
     * Updates modification date
     *
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if post with this id does not exist
     * or account with this username does not exist,
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updateTitle(Long postId, String updaterUsername, String title) throws ResponseStatusException;

    /**
     * Updates modification date
     *
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if post with this id does not exist
     * or account with this username does not exist,
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updateBody(Long postId, String updaterUsername, String body) throws ResponseStatusException;

    /**
     * Sets postedDate if new state is {@link Post.State#POSTED}
     *
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if post with this id does not exist,
     * or account with this username does not exist
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updateState(Long postId, String updaterUsername, Post.State state) throws ResponseStatusException;

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if <br>
     * 1. post with this id does not exist <br>
     * 2. account with this username does not exist <br>
     * 3. any tag does not exist <br>
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updateTags(Long postId, String updaterUsername, List<String> tagNames) throws ResponseStatusException;

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if post with this id does not exist <br>
     * or account with this username does not exist <br>
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updatePostContacts(Long postId, String updaterUsername, ContactList contactList) throws ResponseStatusException;

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if <br>
     * 1. post with this id does not exist <br>
     * 2. account with this username does not exist <br>
     * 3. any user contact (or even the type) does not exist, <br>
     * with {@link HttpStatus#FORBIDDEN} if updater is not permitted to modify state
     */
    Post updatePostUserContacts(Long postId, String updaterUsername, ContactList contactList) throws ResponseStatusException;

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if post with this id does not exist
     * or account with this username does not exist <br>
     * with {@link HttpStatus#FORBIDDEN} if receiver can not receive this data
     */
    PostInfo getPostInfo(Long postId, String receiverUsername) throws ResponseStatusException;

    void addPostToFavourites(Long postId, String receiverUsername);

    boolean removePostFromFavourites(Long postId, String receiverUsername);

    // todo: receive post data
    // todo: paging with specified tags

    // todo: favouritePosts, subs, and home posts receive
}
