package ru.combuddy.backend.controllers.post.service.interfaces;

import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;
import ru.combuddy.backend.exceptions.general.IllegalPageNumberException;
import ru.combuddy.backend.exceptions.permission.DeleteNotPermittedException;
import ru.combuddy.backend.exceptions.permission.ReceiveNotPermittedException;
import ru.combuddy.backend.exceptions.permission.post.PostContentUpdateNotPermittedException;
import ru.combuddy.backend.exceptions.permission.post.PostStateUpdateNotPermittedException;
import ru.combuddy.backend.exceptions.post.IllegalCreationDataException;
import ru.combuddy.backend.exceptions.post.IllegalPostStateException;
import ru.combuddy.backend.exceptions.post.InvalidPostIdException;
import ru.combuddy.backend.exceptions.permission.post.NotPermittedPostStateException;
import ru.combuddy.backend.exceptions.tag.InvalidTagNameException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.util.List;

public interface PostService {

    /**
     * Sets postedDate if state is {@link Post.State#POSTED}
     */
    Post create(PostCreationData creationData, String ownerUsername)
            throws UserNotExistsException,
            IllegalCreationDataException,
            IllegalPostStateException,
            InvalidTagNameException,
            NotFoundUserContactException;

    void delete(Long postId, String removerUsername)
            throws UserNotExistsException,
            InvalidPostIdException,
            DeleteNotPermittedException;

    /**
     * Updates modification date
     */
    Post updateTitle(Long postId,
                     String updaterUsername,
                     String title)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException;

    /**
     * Updates modification date
     */
    Post updateBody(Long postId,
                    String updaterUsername,
                    String body)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException;

    /**
     * Sets postedDate if new state is {@link Post.State#POSTED}
     */
    Post updateState(Long postId,
                     String updaterUsername,
                     Post.State newState)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostStateUpdateNotPermittedException;

    Post updateTags(Long postId,
                    String updaterUsername,
                    List<String> tagNames)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException,
            InvalidTagNameException;

    Post updatePostContacts(Long postId,
                            String updaterUsername,
                            ContactList postContacts)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException;

    Post updatePostUserContacts(Long postId,
                                String updaterUsername,
                                ContactList userContacts)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException,
            NotFoundUserContactException;

    PostInfo getPostInfo(Long postId, String receiverUsername)
            throws UserNotExistsException,
            InvalidPostIdException,
            ReceiveNotPermittedException;

    PostInfo toPostInfo(Post post, UserAccount receiver);

    Post getById(Long postId) throws InvalidPostIdException;

    List<Long> searchMain(int pageNumberSinceOne,
                          List<String> requestedStateName,
                          String receiverUsername)
            throws IllegalPageNumberException,
            UserNotExistsException,
            IllegalPostStateException,
            NotPermittedPostStateException;

    List<Long> searchHome(int pageNumberSinceOne,
                          List<String> requestedStateNames,
                          String receiverUsername)
            throws IllegalPageNumberException,
                UserNotExistsException,
                IllegalPostStateException,
                NotPermittedPostStateException;

    List<Long> getSubscriptionsFeed(int pageNumberSinceOne,
                                    List<String> requestedStateNames,
                                    String receiverUsername)
            throws IllegalPageNumberException,
            UserNotExistsException,
            IllegalPostStateException,
            NotPermittedPostStateException;

    List<Long> searchFavourites(int pageNumberSinceOne,
                                List<String> requestedStateNames,
                                String receiverUsername)
            throws IllegalPageNumberException,
                UserNotExistsException,
                IllegalPostStateException,
                NotPermittedPostStateException;
}
