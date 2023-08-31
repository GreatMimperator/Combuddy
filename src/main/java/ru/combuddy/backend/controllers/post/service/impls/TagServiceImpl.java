package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.controllers.post.projections.TagNameProjection;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;
import ru.combuddy.backend.entities.post.tag.UserHomeTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.tag.InvalidTagNameException;
import ru.combuddy.backend.exceptions.tag.TagAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.tag.TagRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.combuddy.backend.entities.post.tag.UserHomeTag.FilterType.EXCLUDING;
import static ru.combuddy.backend.entities.post.tag.UserHomeTag.FilterType.INCLUDING;

@Service
@Transactional
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserAccountService userAccountService;

    @Override
    public boolean exists(String name) {
        return tagRepository.existsByName(name);
    }

    @Override
    public Optional<Tag> find(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public List<String> findNamesByNameStartingWith(String nameBeginPart) {
        return tagRepository.findNamesByNameStartingWith(nameBeginPart).stream()
                .map(TagNameProjection::getName)
                .toList();
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<String> getAllNames() {
        return tagRepository.findAllNamesBy().stream()
                .map(TagNameProjection::getName)
                .toList();
    }

    @Override
    public boolean delete(String name) {
        var deletedCount = tagRepository.deleteByName(name);
        return deletedCount > 0;
    }

    @Override
    public Tag add(String name, String description) throws TagAlreadyExistsException {
        if (this.exists(name)) {
            throw new TagAlreadyExistsException("Tag with this name already exists");
        }
        return tagRepository.save(new Tag(null, name, description));
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<PostTag> getPostTagsFromTagNames(List<String> tagNames, Post post)
            throws InvalidTagNameException {
        return tagNames.stream()
                .map(tagName ->
                        new PostTag(
                                null,
                                post,
                                this.getByName(tagName)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<PostTag> getPostTagsFromTags(List<Tag> tags, Post post) {
        return tags.stream()
                .map(tag -> new PostTag(null, post, tag))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Tag> getTagsFromHomeTags(List<UserHomeTag> homeTags) {
        return homeTags.stream()
                .map(UserHomeTag::getTag)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getTagNamesFromPostTags(List<PostTag> tags) {
        return tags.stream()
                .map(PostTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void homeTagsUpdate(List<String> includedTagNames,
                               List<String> excludedTagNames,
                               String updaterUsername)
            throws UserNotExistsException,
            InvalidTagNameException {
        var updater = userAccountService.getByUsername(updaterUsername);
        addNewHomeTagsRemoveNotActual(includedTagNames, excludedTagNames, updater);
        userAccountService.save(updater);
    }

    @Override
    public void addNewHomeTagsRemoveNotActual(List<String> actualIncludedHomeTags,
                                              List<String> actualExcludedHomeTags,
                                              UserAccount updater)
            throws InvalidTagNameException {
        var removedTags = new LinkedList<UserHomeTag>();
        for (var oldHomeTag : updater.getHomeTags()) {
            if (!isActual(actualIncludedHomeTags, actualExcludedHomeTags, oldHomeTag)) {
                oldHomeTag.setUser(null);
                removedTags.add(oldHomeTag);
            }
        }
        updater.getHomeTags().removeAll(removedTags);
        var remainingHomeTags = getTagNamesFromHomeTags(updater.getHomeTags());
        actualIncludedHomeTags.removeAll(remainingHomeTags);
        actualExcludedHomeTags.removeAll(remainingHomeTags);
        updater.getHomeTags().addAll(
                getHomeTagsFromTagNames(
                        actualIncludedHomeTags,
                        INCLUDING,
                        updater));
        updater.getHomeTags().addAll(
                getHomeTagsFromTagNames(
                        actualExcludedHomeTags,
                        EXCLUDING,
                        updater));
    }

    private static boolean isActual(List<String> actualIncludedHomeTags,
                                    List<String> actualExcludedHomeTags,
                                    UserHomeTag oldHomeTag) {
        boolean isActual = false;
        switch (oldHomeTag.getFilterType()) {
            case INCLUDING -> {
                if (actualIncludedHomeTags.contains(oldHomeTag.getTag().getName())) {
                    isActual = true;
                }
            }
            case EXCLUDING -> {
                if (actualExcludedHomeTags.contains(oldHomeTag.getTag().getName())) {
                    isActual = true;
                }
            }
        }
        return isActual;
    }

    @Override
    public List<UserHomeTag> getHomeTagsFromTagNames(List<String> tagNames,
                                                     UserHomeTag.FilterType filterType,
                                                     UserAccount userAccount)
            throws InvalidTagNameException {
        return tagNames.stream()
                .map(tagName ->
                        new UserHomeTag(
                            null,
                            userAccount,
                            this.getByName(tagName),
                            filterType))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getTagNamesFromHomeTags(List<UserHomeTag> homeTags) {
        return homeTags.stream()
                .map(UserHomeTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public FilterTags getFilterTags(String receiverUsername) throws UserNotExistsException {
        var receiver = userAccountService.getByUsername(receiverUsername);
        return getFilterTags(receiver.getHomeTags());
    }

    @Override
    public FilterTags getFilterTags(List<UserHomeTag> homeTags) {
        var includedTagNames = new LinkedList<String>();
        var excludedTagNames = new LinkedList<String>();
        for (var homeTag : homeTags) {
            var tagName = homeTag.getTag().getName();
            switch (homeTag.getFilterType()) {
                case INCLUDING -> includedTagNames.add(tagName);
                case EXCLUDING -> excludedTagNames.add(tagName);
            }
        }
        return new FilterTags(includedTagNames, excludedTagNames);
    }

    @Override
    public Tag getByName(String name) throws InvalidTagNameException {
        var foundTag = tagRepository.findByName(name);
        if (foundTag.isEmpty()) {
            throw new InvalidTagNameException("Tag with this name does not exist");
        }
        return foundTag.get();
    }

    @Override
    public void put(String name, String description) {
        if (!this.exists(name)) {
            this.add(name, description);
        }
    }

    @Override
    public void updateDescription(String name, String description) throws InvalidTagNameException {
        var tag = this.getByName(name);
        tag.setDescription(description);
        this.save(tag);
    }

    @Override
    public String getDescription(String name) throws InvalidTagNameException {
        return this.getByName(name).getDescription();
    }
}
