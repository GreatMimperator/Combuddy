package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.ServiceConstants;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostContactService;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostUserContactService;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.controllers.post.projections.PostOwnerUsernameAndIdProjection;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;
import ru.combuddy.backend.exceptions.general.IllegalPageNumberException;
import ru.combuddy.backend.exceptions.permission.DeleteNotPermittedException;
import ru.combuddy.backend.exceptions.permission.ReceiveNotPermittedException;
import ru.combuddy.backend.exceptions.permission.post.NotPermittedPostStateException;
import ru.combuddy.backend.exceptions.permission.post.PostContentUpdateNotPermittedException;
import ru.combuddy.backend.exceptions.permission.post.PostStateUpdateNotPermittedException;
import ru.combuddy.backend.exceptions.post.IllegalCreationDataException;
import ru.combuddy.backend.exceptions.post.IllegalPostStateException;
import ru.combuddy.backend.exceptions.post.InvalidPostIdException;
import ru.combuddy.backend.exceptions.tag.InvalidTagNameException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.post.PostCriteriaRepository;
import ru.combuddy.backend.repositories.post.PostRepository;
import ru.combuddy.backend.security.verifiers.post.FeedPostsWithStateReceiveAbilityVerifier;
import ru.combuddy.backend.security.verifiers.post.PostDeleteVerifier;
import ru.combuddy.backend.security.verifiers.post.change.PostContentChangeVerifier;
import ru.combuddy.backend.security.verifiers.post.change.PostStateChangeVerifier;
import ru.combuddy.backend.security.verifiers.post.receive.PostCreationDateReceiveAbilityVerifier;
import ru.combuddy.backend.security.verifiers.post.receive.PostInfoReceiveAbilityVerifier;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.combuddy.backend.entities.post.Post.State.DRAFT;
import static ru.combuddy.backend.entities.post.Post.State.POSTED;
import static ru.combuddy.backend.entities.post.tag.PostTag.toTagNames;

@Service
@Transactional
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostCriteriaRepository postCriteriaRepository;
    private final UserAccountService userAccountService;
    private final TagService tagService;
    private final PostContactService postContactService;
    private final PostUserContactService postUserContactService;

    private final PostDeleteVerifier postDeleteVerifier;
    private final PostStateChangeVerifier postStateChangeVerifier;
    private final PostContentChangeVerifier postContentChangeVerifier;
    private final PostInfoReceiveAbilityVerifier postInfoReceiveAbilityVerifier;
    private final PostCreationDateReceiveAbilityVerifier postCreationDateReceiveAbilityVerifier;
    private final FeedPostsWithStateReceiveAbilityVerifier feedPostsWithStateReceiveAbilityVerifier;

    public final ServiceConstants serviceConstants;

    @Override
    public Post create(PostCreationData creationData, String ownerUsername)
            throws UserNotExistsException,
            IllegalPostStateException,
            InvalidTagNameException,
            NotFoundUserContactException {
        var post = new Post();
        post.setCreationDate(Calendar.getInstance());
        post.setOwner(userAccountService.getByUsername(ownerUsername));
        post.setTitle(creationData.getTitle());
        post.setBody(creationData.getBody());
        if (!List.of(DRAFT, POSTED).contains(creationData.getState())) {
            throw new IllegalPostStateException("State can be only draft or posted on creation");
        }
        post.setState(creationData.getState());
        if (creationData.getState() == POSTED) {
            post.setPostedDate(Calendar.getInstance());
        }
        post.setTags(tagService.getPostTagsFromTagNames(creationData.getTags(), post));
        if (creationData.getPostContacts() != null) {
            var postBaseContacts = creationData.getPostContacts();
            post.setPostContacts(
                    postContactService.getFromContacts(postBaseContacts, post));
        }
        if (creationData.getUserContacts() != null) {
            var postUserBaseContacts = creationData.getUserContacts();
            post.setPostUserContacts(
                    postUserContactService.getFromContacts(
                            postUserBaseContacts,
                            post,
                            ownerUsername));
        }
        return postRepository.save(post);
    }

    @Override
    public void delete(Long postId, String deleterUsername)
            throws UserNotExistsException,
            InvalidPostIdException,
            DeleteNotPermittedException {
        var deleter = userAccountService.getByUsername(deleterUsername);
        var post = this.getById(postId);
        checkDeleteAbility(deleter, post);
        postRepository.delete(post);
    }

    @Override
    public Post updateTitle(Long postId,
                            String updaterUsername,
                            String title)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkContentUpdateAbility(updater, post);
        post.setTitle(title);
        post.setModificationDate(Calendar.getInstance());
        return postRepository.save(post);
    }

    @Override
    public Post updateBody(Long postId,
                           String updaterUsername,
                           String body)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkContentUpdateAbility(updater, post);
        post.setBody(body);
        post.setModificationDate(Calendar.getInstance());
        return postRepository.save(post);
    }

    @Override
    public Post updateState(Long postId,
                            String updaterUsername,
                            Post.State newState)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostStateUpdateNotPermittedException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkStateUpdateAbility(updater, post, newState);
        post.setState(newState);
        if (newState == POSTED && post.getPostedDate() == null) {
            post.setPostedDate(Calendar.getInstance());
        }
        return postRepository.save(post);
    }

    @Override
    public Post updateTags(Long postId,
                           String updaterUsername,
                           List<String> tagNames)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException,
            InvalidTagNameException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkContentUpdateAbility(updater, post);
        addNewTagsRemoveNotActual(tagNames, post);
        return postRepository.save(post);
    }

    private void addNewTagsRemoveNotActual(List<String> actualTagNames, Post post) throws InvalidTagNameException {
        var removedTags = new LinkedList<PostTag>();
        for (var oldTag : post.getTags()) {
            if (!actualTagNames.contains(oldTag.getTag().getName())) {
                oldTag.setPost(null);
                removedTags.add(oldTag);
            }
        }
        post.getTags().removeAll(removedTags);
        var remainingTagNames = tagService.getTagNamesFromPostTags(post.getTags());
        actualTagNames.removeAll(remainingTagNames);
        post.getTags().addAll(tagService.getPostTagsFromTagNames(actualTagNames, post));
    }

    @Override
    public Post updatePostContacts(Long postId,
                                   String updaterUsername,
                                   ContactList postContacts)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkContentUpdateAbility(updater, post);
        addNewPostContactsRemoveNotActual(postContacts.getContacts(), post);
        return postRepository.save(post);
    }

    private void addNewPostContactsRemoveNotActual(List<BaseContactInfo> actualBaseContacts, Post post) {
        var removedContacts = new LinkedList<PostContact>();
        for (var oldContact : post.getPostContacts()) {
            if (!actualBaseContacts.contains(new BaseContactInfo(oldContact))) {
                oldContact.setPost(null);
                removedContacts.add(oldContact);
            }
        }
        post.getPostContacts().removeAll(removedContacts);
        var remainingContacts = postContactService.getBaseContacts(post.getPostContacts());
        actualBaseContacts.removeAll(remainingContacts);
        post.getPostContacts().addAll(postContactService.getFromContacts(actualBaseContacts, post));
    }

    @Override
    public Post updatePostUserContacts(Long postId,
                                       String updaterUsername,
                                       ContactList userContacts)
            throws UserNotExistsException,
            InvalidPostIdException,
            PostContentUpdateNotPermittedException,
            NotFoundUserContactException {
        var updater = userAccountService.getByUsername(updaterUsername);
        var post = this.getById(postId);
        checkContentUpdateAbility(updater, post);
        addNewPostUserContactsRemoveNotActual(userContacts.getContacts(), post);
        return postRepository.save(post);
    }

    private void addNewPostUserContactsRemoveNotActual(List<BaseContactInfo> actualBaseContacts, Post post)
            throws NotFoundUserContactException {
        var removedContacts = new LinkedList<PostUserContact>();
        for (var oldContact : post.getPostUserContacts()) {
            if (!actualBaseContacts.contains(new BaseContactInfo(oldContact.getUserContact()))) {
                oldContact.setPost(null);
                removedContacts.add(oldContact);
            }
        }
        post.getPostUserContacts().removeAll(removedContacts);
        var remainingContacts = postUserContactService.getBaseContacts(post.getPostUserContacts());
        actualBaseContacts.removeAll(remainingContacts);
        post.getPostUserContacts().addAll(
                postUserContactService.getFromContacts(
                        actualBaseContacts,
                        post,
                        post.getOwner().getUsername()));
    }

    @Override
    public PostInfo getPostInfo(Long postId, String receiverUsername)
            throws UserNotExistsException,
            InvalidPostIdException,
            ReceiveNotPermittedException {
        var receiver = userAccountService.getByUsername(receiverUsername);
        var post = this.getById(postId);
        checkPostInfoReceiveAbility(receiver, post);
        return toPostInfo(post, receiver);
    }

    @Override
    public PostInfo toPostInfo(Post post, UserAccount receiver) {
        var builder = PostInfo.builder();
        builder.id(post.getId());
        builder.ownerUsername(post.getOwner().getUsername());
        builder.title(post.getTitle());
        builder.body(post.getBody());
        builder.state(post.getState());
        builder.tags(toTagNames(post.getTags()));
        builder.postContacts(PostContact.toBaseContactInfo(post.getPostContacts()));
        builder.postUserContacts(PostUserContact.toBaseContactInfo(post.getPostUserContacts()));
        var verifyInfo = new PostCreationDateReceiveAbilityVerifier.VerifyInfo(post.getOwner(), post);
        if (postCreationDateReceiveAbilityVerifier.verify(receiver, verifyInfo)) {
            builder.creationDate(Optional.of(post.getCreationDate()));
        } else {
            builder.creationDate(Optional.empty());
        }
        builder.postedDate(Optional.ofNullable(post.getPostedDate()));
        if (post.getPostedDate() != null && post.getModificationDate() != null &&
                post.getPostedDate().after(post.getModificationDate())) {
            builder.modificationDate(Optional.empty());
        } else {
            builder.modificationDate(Optional.ofNullable(post.getModificationDate()));
        }
        return builder.build();
    }

    @Override
    public Post getById(Long postId) throws InvalidPostIdException {
        var foundPost = postRepository.findById(postId);
        if (foundPost.isEmpty()) {
            throw new InvalidPostIdException("Post with this id do not exist");
        }
        return foundPost.get();
    }


    @Override
    public List<Long> searchMain(int pageNumberSinceOne,
                                 List<String> requestedStateNames,
                                 String receiverUsername)
            throws IllegalPageNumberException,
            UserNotExistsException,
            IllegalPostStateException,
            NotPermittedPostStateException {
        var pageRequest = serviceConstants.pageRequest(pageNumberSinceOne);
        var requestedStates = namesToPostStates(requestedStateNames);
        var receiver = userAccountService.getByUsername(receiverUsername);
        checkRequestedStates(requestedStates, receiver);
        var searchParams = PostCriteriaRepository.SearchParams.builder()
                .allowedStates(requestedStates)
                .receiver(receiver)
                .filterAggressors(true)
                .pageable(pageRequest)
                .build();
        return postCriteriaRepository.searchBy(searchParams).toList();
    }


    @Override
    public List<Long> searchHome(int pageNumberSinceOne,
                                 List<String> requestedStateNames,
                                 String receiverUsername)
            throws IllegalPageNumberException,
            UserNotExistsException,
            IllegalPostStateException,
            NotPermittedPostStateException {
        var pageRequest = serviceConstants.pageRequest(pageNumberSinceOne);
        var receiver = userAccountService.getByUsername(receiverUsername);
        var filterTags = tagService.getFilterTags(receiver.getHomeTags());
        var requestedStates = namesToPostStates(requestedStateNames);
        checkRequestedStates(requestedStates, receiver);
        var searchParams = PostCriteriaRepository.SearchParams.builder()
                .includedTagNames(filterTags.getIncludeTagNames())
                .excludedTagNames(filterTags.getExcludeTagNames())
                .allowedStates(requestedStates)
                .receiver(receiver)
                .filterAggressors(true)
                .pageable(pageRequest)
                .build();
        return postCriteriaRepository.searchBy(searchParams).toList();
    }

    @Override
    public List<Long> getSubscriptionsFeed(int pageNumberSinceOne,
                                           List<String> requestedStateNames,
                                           String receiverUsername)
            throws IllegalPageNumberException,
            UserNotExistsException,
            IllegalPostStateException,
            NotPermittedPostStateException {
        var pageRequest = serviceConstants.pageRequest(pageNumberSinceOne);
        var receiver = userAccountService.getByUsername(receiverUsername);
        var filterTags = tagService.getFilterTags(receiver.getHomeTags());
        var requestedStates = namesToPostStates(requestedStateNames);
        checkRequestedStates(requestedStates, receiver);
        var searchParams = PostCriteriaRepository.SearchParams.builder()
                .includedTagNames(filterTags.getIncludeTagNames())
                .excludedTagNames(filterTags.getExcludeTagNames())
                .allowedStates(requestedStates)
                .receiver(receiver)
                .filterSubscriptions(true)
                .pageable(pageRequest)
                .build();
        return postCriteriaRepository.searchBy(searchParams).toList();
    }

    @Override
    public List<Long> searchFavourites(int pageNumberSinceOne,
                                       List<String> requestedStateNames,
                                       String receiverUsername) throws IllegalPageNumberException, UserNotExistsException, IllegalPostStateException, NotPermittedPostStateException {
        var pageRequest = serviceConstants.pageRequest(pageNumberSinceOne);
        var receiver = userAccountService.getByUsername(receiverUsername);
        var filterTags = tagService.getFilterTags(receiver.getHomeTags());
        var requestedStates = namesToPostStates(requestedStateNames);
        var searchParams = PostCriteriaRepository.SearchParams.builder()
                .includedTagNames(filterTags.getIncludeTagNames())
                .excludedTagNames(filterTags.getExcludeTagNames())
                .allowedStates(requestedStates)
                .receiver(receiver)
                .filterFavourites(true)
                .pageable(pageRequest)
                .build();
        return postCriteriaRepository.searchBy(searchParams).toList();
    }


    private void checkRequestedStates(List<Post.State> states, UserAccount asker) {
        for (var state : states) {
            if (!feedPostsWithStateReceiveAbilityVerifier.verify(asker, state)) {
                throw new NotPermittedPostStateException("Not permitted receive because of requested state");
            }
        }
    }

    private List<Post.State> namesToPostStates(List<String> stateNames) {
        return stateNames.stream()
                .map(Post.State::convertToState)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Long> searchResultPageToIds(Stream<PostOwnerUsernameAndIdProjection> projectionStream) {
        return projectionStream
                .map(PostOwnerUsernameAndIdProjection::getId)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Long> searchResultPageToIds(Page<PostOwnerUsernameAndIdProjection> post) {
        return this.searchResultPageToIds(post.get());
    }

    public void checkDeleteAbility(UserAccount deleter, Post post) throws DeleteNotPermittedException {
        if (!postDeleteVerifier.verify(deleter, post)) {
            throw new DeleteNotPermittedException("User can not delete this post");
        }
    }

    private void checkPostInfoReceiveAbility(UserAccount receiver, Post post) throws ReceiveNotPermittedException {
        if (!postInfoReceiveAbilityVerifier.verify(receiver, post)) {
            throw new ReceiveNotPermittedException("User can not receive this post");
        }
    }

    private void checkStateUpdateAbility(UserAccount updater,
                                         Post post,
                                         Post.State newState)
            throws PostStateUpdateNotPermittedException {
        var oldState = post.getState();
        var verifyInfo = new PostStateChangeVerifier.VerifyInfo(post.getOwner(), oldState, newState);
        if (!postStateChangeVerifier.verify(updater, verifyInfo)) {
            throw new PostStateUpdateNotPermittedException("User can not change this post state");
        }
    }

    public void checkContentUpdateAbility(UserAccount updater, Post post)
            throws PostContentUpdateNotPermittedException {
        var verifyInfo = new PostContentChangeVerifier.VerifyInfo(post.getOwner(), post.getState());
        if (!postContentChangeVerifier.verify(updater, verifyInfo)) {
            throw new PostContentUpdateNotPermittedException("User can not change this post content");
        }
    }
}
