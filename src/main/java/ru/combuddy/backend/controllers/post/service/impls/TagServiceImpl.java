package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.controllers.post.projections.TagNameProjection;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;
import ru.combuddy.backend.entities.post.tag.UserHomeTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.repositories.tag.TagRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.combuddy.backend.controllers.user.UserAccountController.checkFoundAccount;
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
    public Tag add(String name, String description) {
        return tagRepository.save(new Tag(null, name, description));
    }

    @Override
    public Tag addWithExistenceCheck(String name, String description) throws AlreadyExistsException {
        if (tagRepository.existsByName(name)) {
            throw new AlreadyExistsException("Tag with this name already exists", name);
        }
        return add(name, description);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<PostTag> getPostTagsFromTagNames(List<String> tagNames, Post post)
            throws IllegalArgumentException {
        try {
            return tagNames.stream()
                    .map(tagName -> new PostTag(null, post, tagRepository.findByName(tagName).get()))
                    .collect(Collectors.toCollection(LinkedList::new));
        } catch (NoSuchElementException e) { // catches Optional.get()
            throw new IllegalArgumentException("creationData contains wrong tag name in post tags list");
        }
    }

    @Override
    public List<String> getTagNamesFromPostTags(List<PostTag> tags) {
        return tags.stream()
                .map(PostTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void homeTagsUpdate(List<String> includedTagNames, List<String> excludedTagNames, String updaterUsername) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        try {
            addNewHomeTagsRemoveNotActual(includedTagNames, excludedTagNames, updater);
            userAccountService.save(updater);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one of included / excluded tags does not exist");
        }
    }

    @Override
    public void addNewHomeTagsRemoveNotActual(List<String> actualIncludedHomeTags,
                                              List<String> actualExcludedHomeTags,
                                              UserAccount updater) {
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
        updater.getHomeTags().addAll(getHomeTagsFromTagNames(actualIncludedHomeTags,
                INCLUDING,
                updater));
        updater.getHomeTags().addAll(getHomeTagsFromTagNames(actualExcludedHomeTags,
                EXCLUDING,
                updater));
    }

    private static boolean isActual(List<String> actualIncludedHomeTags, List<String> actualExcludedHomeTags, UserHomeTag oldHomeTag) {
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

    public List<UserHomeTag> getHomeTagsFromTagNames(List<String> tags,
                                                     UserHomeTag.FilterType filterType,
                                                     UserAccount userAccount)
            throws IllegalArgumentException {
        try {
            return tags.stream()
                    .map(tagName -> new UserHomeTag(null,
                            userAccount,
                            tagRepository.findByName(tagName).get(),
                            filterType))
                    .collect(Collectors.toCollection(LinkedList::new));
        } catch (NoSuchElementException e) { // catches Optional.get()
            throw new IllegalArgumentException("creationData contains wrong tag name in post tags list");
        }
    }

    @Override
    public List<String> getTagNamesFromHomeTags(List<UserHomeTag> homeTags) {
        return homeTags.stream()
                .map(UserHomeTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public FilterTags getHomeTags(String receiverUsername) throws ResponseStatusException {
        var receiver = checkFoundAccount(userAccountService.findByUsername(receiverUsername));
        var homeTags = receiver.getHomeTags();
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
}
