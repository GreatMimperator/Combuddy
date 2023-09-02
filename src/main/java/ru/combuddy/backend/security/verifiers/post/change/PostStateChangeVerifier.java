package ru.combuddy.backend.security.verifiers.post.change;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.RoleName;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class PostStateChangeVerifier implements PermissionVerifier<PostStateChangeVerifier.VerifyInfo> {

    private final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        return switch (target.oldState) {
            case POSTED -> verifyIfOldStatePosted(asker, target);
            case DRAFT -> verifyIfOldStateDraft(asker, target);
            case HIDDEN -> verifyIfOldStateHidden(asker, target);
            case FROZEN -> verifyIfOldStateFrozen(asker, target);
        };
    }

    private boolean verifyIfOldStatePosted(UserAccount asker, VerifyInfo target) {
        var checksSelf = asker.equals(target.postOwner);
        return switch (target.stateToSet) {
            case POSTED -> true; // the same state
            case DRAFT -> false; // posted can be translated to hidden, not to draft
            case HIDDEN -> checksSelf;
            case FROZEN -> verifyFreezeAuthority(asker, target);
        };
    }

    private boolean verifyIfOldStateDraft(UserAccount asker, VerifyInfo target) {
        var checksSelf = asker.equals(target.postOwner);
        return switch (target.stateToSet) {
            case DRAFT -> true; // the same state
            case POSTED -> checksSelf;
            case HIDDEN -> false; // draft can be translated to posted, not to hidden
            case FROZEN -> false; // nobody sees it - no way to ban
        };
    }

    private boolean verifyIfOldStateHidden(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.postOwner);
        return switch (target.oldState) {
            case HIDDEN -> true; // the same state
            case POSTED -> checksSelf;
            case DRAFT -> false; // already hidden - no need in recreation
            case FROZEN -> false; // ban user in case of (hidden -> posted -> hidden) trolling
        };
    }

    private boolean verifyIfOldStateFrozen(UserAccount asker, VerifyInfo target) {
        return switch (target.oldState) {
            case FROZEN -> true; // the same state
            case POSTED -> verifyFreezeAuthority(asker, target);
            case DRAFT, HIDDEN -> false; // can not freeze posts in these states
        };
    }

    private boolean verifyFreezeAuthority(UserAccount asker, VerifyInfo target) {
        var checksSelf = asker.equals(target.postOwner);
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        var isAskerOverTarget = authorityComparator.compare(asker, target.postOwner) > 0;
        var enoughAuthority = isAskerOverTarget && isAskerModeratorOrOver;
        return !checksSelf && enoughAuthority;
    }

    @Getter
    public static class VerifyInfo {
        final private UserAccount postOwner;
        final private Post.State oldState;
        final private Post.State stateToSet;

        public VerifyInfo(UserAccount postOwner, Post.State oldState, Post.State stateToSet) {
            this.postOwner = postOwner;
            this.oldState = oldState;
            this.stateToSet = stateToSet;
        }
    }
}
