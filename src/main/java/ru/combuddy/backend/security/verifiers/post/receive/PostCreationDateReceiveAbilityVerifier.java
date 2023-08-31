package ru.combuddy.backend.security.verifiers.post.receive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MAIN_MODERATOR;

@Component
@AllArgsConstructor
public class PostCreationDateReceiveAbilityVerifier implements PermissionVerifier<PostCreationDateReceiveAbilityVerifier.VerifyInfo> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.getOwner());
        return authorityComparator.compare(asker, ROLE_MAIN_MODERATOR) >= 0 ||
                checksSelf;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyInfo {
        private final UserAccount owner;
        private final Post post;
    }
}
