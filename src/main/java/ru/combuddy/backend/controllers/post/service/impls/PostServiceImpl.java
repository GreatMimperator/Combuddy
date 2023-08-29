package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostContactService;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostUserContactService;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.controllers.post.models.TagNames;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.FavoritePost;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.repositories.post.FavoritePostRepository;
import ru.combuddy.backend.repositories.post.PostRepository;
import ru.combuddy.backend.security.verifiers.post.PostDeleteVerifier;
import ru.combuddy.backend.security.verifiers.post.change.PostContentChangeVerifier;
import ru.combuddy.backend.security.verifiers.post.change.PostStateChangeVerifier;
import ru.combuddy.backend.security.verifiers.post.receive.PostCreationDateReceiveAbilityVerifier;
import ru.combuddy.backend.security.verifiers.post.receive.PostInfoReceiveAbilityVerifier;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static ru.combuddy.backend.controllers.user.UserAccountController.checkFoundAccount;
import static ru.combuddy.backend.entities.post.Post.State.*;
import static ru.combuddy.backend.entities.post.tag.PostTag.toTagNames;

@Service
@Transactional
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private UserAccountService userAccountService;
    private PostRepository postRepository;
    private TagService tagService;
    private PostContactService postContactService;
    private PostUserContactService postUserContactService;
    private FavoritePostRepository favoritePostRepository;

    private PostDeleteVerifier postDeleteVerifier;
    private PostStateChangeVerifier postStateChangeVerifier;
    private PostContentChangeVerifier postContentChangeVerifier;
    private PostInfoReceiveAbilityVerifier postInfoReceiveAbilityVerifier;
    private PostCreationDateReceiveAbilityVerifier postCreationDateReceiveAbilityVerifier;

    @Override
    public Post create(PostCreationData creationData, String ownerUsername)
            throws ResponseStatusException {
        var post = new Post();
        post.setCreationDate(Calendar.getInstance());
        post.setOwner(checkFoundAccount(userAccountService.findByUsername(ownerUsername)));
        post.setTitle(creationData.getTitle());
        post.setBody(creationData.getBody());
        if (!List.of(DRAFT, POSTED).contains(creationData.getState())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can create post with draft and posted states only");
        }
        post.setState(creationData.getState());
        if (creationData.getState() == POSTED) {
            post.setPostedDate(Calendar.getInstance());
        }
        try {
            post.setTags(tagService.getPostTagsFromTagNames(creationData.getTagNames(), post));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one post tag does not exist");
        }
        if (creationData.getPostContacts().isPresent()) {
            post.setPostContacts(postContactService.getFromContacts(creationData.getPostContacts().get(), post));
        }
        if (creationData.getPostUserContacts().isPresent()) {
            var postUserContacts = creationData.getPostUserContacts().get();
            try {
                post.setPostUserContacts(postUserContactService.getFromContacts(postUserContacts, post, ownerUsername));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "At least one user contact does not exist");
            }
        }
        return postRepository.save(post);
    }

    @Override
    public void delete(Long postId, String removerUsername) {
        var remover = checkFoundAccount(userAccountService.findByUsername(removerUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkDeleteAbility(remover, post);
        postRepository.delete(post);
    }

    @Override
    public Post updateTitle(Long postId, String updaterUsername, String title) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkContentUpdateAbility(updater, post);
        post.setTitle(title);
        post.setModificationDate(Calendar.getInstance());
        return postRepository.save(post);
    }

    @Override
    public Post updateBody(Long postId, String updaterUsername, String body) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkContentUpdateAbility(updater, post);
        post.setBody(body);
        post.setModificationDate(Calendar.getInstance());
        return postRepository.save(post);
    }

    @Override
    public Post updateState(Long postId, String updaterUsername, Post.State newState) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkStateUpdateAbility(updater, post, newState);
        post.setState(newState);
        if (newState == POSTED && post.getPostedDate() == null) {
            post.setPostedDate(Calendar.getInstance());
        }
        return postRepository.save(post);
    }

    @Override
    public Post updateTags(Long postId, String updaterUsername, List<String> tagNames) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkContentUpdateAbility(updater, post);
        try {
            addNewTagsRemoveNotActual(tagNames, post);
            return postRepository.save(post);
        } catch(IllegalArgumentException e) { // todo: ResponseStatusExceptionContainer (тот же текст во многих местах - это плохо)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one post tag does not exist");
        }
    }

    private void addNewTagsRemoveNotActual(List<String> actualTagNames, Post post) {
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
    public Post updatePostContacts(Long postId, String updaterUsername, ContactList postContacts) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
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
    public Post updatePostUserContacts(Long postId, String updaterUsername, ContactList userContacts) throws ResponseStatusException {
        var updater = checkFoundAccount(userAccountService.findByUsername(updaterUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkContentUpdateAbility(updater, post);
        try {
            addNewPostUserContactsRemoveNotActual(userContacts.getContacts(), post);
            return postRepository.save(post);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one user contact does not exist");
        }
    }

    private void addNewPostUserContactsRemoveNotActual(List<BaseContactInfo> actualBaseContacts, Post post) {
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
        post.getPostUserContacts().addAll(postUserContactService.getFromContacts(actualBaseContacts,
                post,
                post.getOwner().getUsername()));
    }

    @Override
    public PostInfo getPostInfo(Long postId, String receiverUsername) throws ResponseStatusException {
        var receiver = checkFoundAccount(userAccountService.findByUsername(receiverUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        checkPostInfoReceiveAbility(receiver, post);
        var builder = PostInfo.builder();
        builder.id(postId);
        builder.ownerUsername(post.getOwner().getUsername());
        builder.title(post.getTitle());
        builder.body(post.getBody());
        builder.state(post.getState());
        builder.tagNames(toTagNames(post.getTags()));
        builder.postContacts(PostContact.toBaseContactInfo(post.getPostContacts()));
        builder.postUserContacts(PostUserContact.toBaseContactInfo(post.getPostUserContacts()));
        if (postCreationDateReceiveAbilityVerifier.verify(receiver, post)) {
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
    public void addPostToFavourites(Long postId, String subscriberUsername) {
        var subscriber = checkFoundAccount(userAccountService.findByUsername(subscriberUsername));
        var post = checkPostFoundById(postRepository.findById(postId));
        if (!favoritePostRepository.existsByPostIdAndSubscriberId(postId, subscriber.getId())) {
            favoritePostRepository.save(new FavoritePost(null, post, subscriber));
        }
    }

    @Override
    public boolean removePostFromFavourites(Long postId, String subscriberUsername) {
        var deletedCount = favoritePostRepository.deleteByPostIdAndSubscriberUsername(postId, subscriberUsername);
        return deletedCount > 0;
    }

    public static Post checkPostFoundById(Optional<Post> foundPost) throws ResponseStatusException {
        if (foundPost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Post with this id do not exist");
        }
        return foundPost.get();
    }

    /**
     * @throws ResponseStatusException with {@link HttpStatus#FORBIDDEN} if verify failed
     */
    public void checkDeleteAbility(UserAccount remover, Post post) throws ResponseStatusException {
        if (!postDeleteVerifier.verify(remover, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not delete this post");
        }
    }

    private void checkPostInfoReceiveAbility(UserAccount receiver, Post post) throws ResponseStatusException {
        if (!postInfoReceiveAbilityVerifier.verify(receiver, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not receive this post");
        }
    }

    /**
     * @throws ResponseStatusException with {@link HttpStatus#FORBIDDEN} if verify failed
     */
    private void checkStateUpdateAbility(UserAccount updater, Post post, Post.State newState) throws ResponseStatusException {
        var oldState = post.getState();
        var verifyInfo = new PostStateChangeVerifier.VerifyInfo(post.getOwner(), oldState, newState);
        if (!postStateChangeVerifier.verify(updater, verifyInfo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    MessageFormat.format("You can not change post state to {0}",
                            newState.name()));
        }
    }

    /**
     * @throws ResponseStatusException with {@link HttpStatus#FORBIDDEN} if verify failed
     */
    public void checkContentUpdateAbility(UserAccount updater, Post post) throws ResponseStatusException {
        var verifyInfo = new PostContentChangeVerifier.VerifyInfo(post.getOwner(), post.getState());
        if (!postContentChangeVerifier.verify(updater, verifyInfo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not update contents of this post");
        }
    }
}
