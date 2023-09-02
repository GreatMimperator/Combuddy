package ru.combuddy.backend.security.verifiers.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class FeedPostsWithStateReceiveAbilityVerifier implements PermissionVerifier<Post.State> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, Post.State target) {
        return switch (target) {
            case POSTED -> true;
            case DRAFT, HIDDEN -> false;
            case FROZEN -> authorityComparator.overOrEqual(asker, ROLE_MODERATOR);
        };
    }
}
