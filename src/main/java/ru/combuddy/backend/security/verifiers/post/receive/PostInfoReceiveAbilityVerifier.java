package ru.combuddy.backend.security.verifiers.post.receive;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class PostInfoReceiveAbilityVerifier implements PermissionVerifier<Post> {

    public final Role.RoleName.AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, Post target) {
        var postOwner = target.getOwner();
        boolean checksSelf = asker.equals(postOwner);
        return switch(target.getState()) {
            case POSTED -> true;
            case DRAFT, HIDDEN -> checksSelf;
            case FROZEN -> authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        };
    }
}
