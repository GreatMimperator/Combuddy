package ru.combuddy.backend.security.verifiers.post.change;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

@Component
@AllArgsConstructor
public class PostContentChangeVerifier implements PermissionVerifier<PostContentChangeVerifier.VerifyInfo> {

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        var checksSelf = asker.equals(target.postOwner);
        return switch (target.postState) {
            case POSTED, HIDDEN, DRAFT -> checksSelf;
            case FROZEN -> false;
        };
    }

    @Getter
    public static class VerifyInfo {
        final private UserAccount postOwner;
        final private Post.State postState;

        public VerifyInfo(UserAccount postOwner, Post.State postState) {
            this.postOwner = postOwner;
            this.postState = postState;
        }
    }
}
