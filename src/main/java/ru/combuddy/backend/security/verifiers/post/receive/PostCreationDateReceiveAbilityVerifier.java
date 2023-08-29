package ru.combuddy.backend.security.verifiers.post.receive;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MAIN_MODERATOR;

@Component
@AllArgsConstructor
public class PostCreationDateReceiveAbilityVerifier implements PermissionVerifier<Post> {

    public final Role.RoleName.AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, Post target) { // todo: add VerifyInfo
        boolean checksSelf = asker.equals(target.getOwner());
        return authorityComparator.compare(asker, ROLE_MAIN_MODERATOR) >= 0 ||
                checksSelf;
    }
}
