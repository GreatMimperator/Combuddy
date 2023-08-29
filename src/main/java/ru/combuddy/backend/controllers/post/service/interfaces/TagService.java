package ru.combuddy.backend.controllers.post.service.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;
import ru.combuddy.backend.entities.post.tag.UserHomeTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface TagService {
    boolean exists(String name);

    Optional<Tag> find(String name);

    List<String> findNamesByNameStartingWith(String nameBeginPart);

    List<Tag> getAll();

    List<String> getAllNames();

    boolean delete(String name);

    /**
     * Can throw SQLException if tag with this name already exist <br>
     * Use {@link #addWithExistenceCheck(String, String)} instead if you want to provide this check
     */
    Tag add(String name, String description);

    Tag addWithExistenceCheck(String name, String description) throws AlreadyExistsException;

    Tag save(Tag tag);

    /**
     * @throws IllegalArgumentException if any tag with tag name does not exist
     */
    List<PostTag> getPostTagsFromTagNames(List<String> tagNames, Post post)
            throws IllegalArgumentException;

    List<String> getTagNamesFromPostTags(List<PostTag> tags);

    /**
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if user does not exist
     * or included / excluded tag does not exist
     */
    void homeTagsUpdate(List<String> includedTags, List<String> excludedTags, String updaterUsername)
            throws ResponseStatusException;

    void addNewHomeTagsRemoveNotActual(List<String> actualIncludedHomeTags,
                                       List<String> actualExcludedHomeTags,
                                       UserAccount updater);

    /**
     * @throws IllegalArgumentException if any tag with tag name does not exist
     */
    List<UserHomeTag> getHomeTagsFromTagNames(List<String> tags,
                                              UserHomeTag.FilterType filterType,
                                              UserAccount userAccount)
            throws IllegalArgumentException;

    List<String> getTagNamesFromHomeTags(List<UserHomeTag> homeTags);

    FilterTags getHomeTags(String receiverUsername);
}
