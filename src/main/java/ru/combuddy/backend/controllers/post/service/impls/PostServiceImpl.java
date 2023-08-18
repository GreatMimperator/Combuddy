package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.post.PostRepository;

import java.util.Optional;

import static ru.combuddy.backend.controllers.user.UserAccountController.checkFoundAccount;

@Service
@Transactional
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private UserAccountService userAccountService;
    private PostRepository postRepository;
    private Validator validator;

    @Override
    public void create(Post post, String creatorUsername) throws NotExistsException, ResponseStatusException {
        var creatorAccount = checkFoundAccount(userAccountService.findByUsername(creatorUsername));
        post.setOwner(creatorAccount);
        var errors = new BeanPropertyBindingResult(post, "post");
        validator.validate(post, errors);
        if (errors.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Post data validation error"); // todo: "Spring Validation Message Interpolation"
        }
        postRepository.save(post);
    }

    @Override
    public void updateArchivedState(Long postId, String archivistUsername, boolean archived) throws NotExistsException {
        var archivistAccount = checkFoundAccount(userAccountService.findByUsername(archivistUsername));
        var foundPost = postRepository.findById(postId);
        var post = checkPostFoundById(foundPost);
        if (!post.getOwner().equals(archivistAccount)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not archive a post you don't own");
        }
        post.setArchived(archived);
        postRepository.save(post);
    }

    public static Post checkPostFoundById(Optional<Post> foundPost) {
        if (foundPost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Post with this id do not exist");
        }
        return foundPost.get();
    }
}
