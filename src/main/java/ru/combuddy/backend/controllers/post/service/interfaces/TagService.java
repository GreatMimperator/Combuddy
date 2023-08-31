package ru.combuddy.backend.controllers.post.service.interfaces;

import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;
import ru.combuddy.backend.entities.post.tag.UserHomeTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.tag.InvalidTagNameException;
import ru.combuddy.backend.exceptions.tag.TagAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.util.List;
import java.util.Optional;

public interface TagService {
    boolean exists(String name);

    Optional<Tag> find(String name);

    List<String> findNamesByNameStartingWith(String nameBeginPart);

    List<Tag> getAll();

    List<String> getAllNames();

    boolean delete(String name);

    Tag add(String name, String description) throws TagAlreadyExistsException;

    Tag save(Tag tag);

    /**
     * @return modifiable list
     */
    List<PostTag> getPostTagsFromTagNames(List<String> tagNames, Post post)
            throws InvalidTagNameException;

    /**
     * @return modifiable list
     */
    List<PostTag> getPostTagsFromTags(List<Tag> tags, Post post);

    /**
     * @return modifiable list
     */
    List<Tag> getTagsFromHomeTags(List<UserHomeTag> homeTags);

    /**
     * @return modifiable list
     */
    List<String> getTagNamesFromPostTags(List<PostTag> tags);

    void homeTagsUpdate(List<String> includedTagNames,
                        List<String> excludedTagNames,
                        String updaterUsername)
            throws UserNotExistsException,
            InvalidTagNameException;

    void addNewHomeTagsRemoveNotActual(List<String> actualIncludedHomeTags,
                                       List<String> actualExcludedHomeTags,
                                       UserAccount updater)
            throws InvalidTagNameException;

    /**
     * @return modifiable list
     */
    List<UserHomeTag> getHomeTagsFromTagNames(List<String> tagNames,
                                              UserHomeTag.FilterType filterType,
                                              UserAccount userAccount)
            throws InvalidTagNameException;

    /**
     * @return modifiable list
     */
    List<String> getTagNamesFromHomeTags(List<UserHomeTag> homeTags);

    FilterTags getFilterTags(String receiverUsername) throws UserNotExistsException;

    FilterTags getFilterTags(List<UserHomeTag> homeTags);

    Tag getByName(String name) throws InvalidTagNameException;

    void put(String name, String description);

    void updateDescription(String name, String description) throws InvalidTagNameException;

    String getDescription(String name) throws InvalidTagNameException;
}
