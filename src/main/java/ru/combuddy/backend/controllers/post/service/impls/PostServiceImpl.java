package ru.combuddy.backend.controllers.post.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.repositories.post.PostRepository;

@Service
@Transactional
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private UserAccountService userAccountService;
    private PostRepository postRepository;

    @Override
    public boolean create(PostCreationData postCreationData) {
        var foundOwner = userAccountService.findByUsername(postCreationData.getPosterUsername());
        if (foundOwner.isEmpty()) {
            return false;
        }
        var post = postCreationData.getPost();
        post.setOwner(foundOwner.get());
        postRepository.save(post);
        return true;
    }

    @Override
    public boolean updateArchivedState(Long postId, boolean archived) {
        var foundPost = postRepository.findById(postId);
        if (foundPost.isEmpty()) {
            return false;
        }
        var post = foundPost.get();
        post.setArchived(archived);
        postRepository.save(post);
        return true;
    }
}
