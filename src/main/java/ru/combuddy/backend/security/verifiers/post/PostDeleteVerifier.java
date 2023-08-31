package ru.combuddy.backend.security.verifiers.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MAIN_MODERATOR;
import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class PostDeleteVerifier implements PermissionVerifier<Post> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, Post target) {
        var postOwner = target.getOwner();
        boolean checksSelf = asker.equals(postOwner);
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        var isAskerMainModeratorOrOver = authorityComparator.compare(asker, ROLE_MAIN_MODERATOR) >= 0;
        var isAskerOverOwner = authorityComparator.compare(asker, target.getOwner()) > 0;
        return switch(target.getState()) {
            case POSTED -> checksSelf || (isAskerModeratorOrOver && isAskerOverOwner);
            case DRAFT, HIDDEN -> checksSelf; // simply ban user on hidden -> posted -> hidden trolling
            case FROZEN -> isAskerMainModeratorOrOver && isAskerOverOwner;
        };
    }
}
