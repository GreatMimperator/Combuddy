package ru.combuddy.backend.security.verifiers.users.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.entities.user.PrivacyPolicy.SubscriptionsAccessLevel.EVERYBODY;
import static ru.combuddy.backend.security.RoleName.ROLE_MAIN_MODERATOR;
import static ru.combuddy.backend.security.RoleName.ROLE_USER;

@Component
@AllArgsConstructor
public class SubscriptionsAccessVerifier implements PermissionVerifier<SubscriptionsAccessVerifier.VerifyInfo> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.userAccount);
        var isAskerMainModeratorOrOver = authorityComparator.compare(asker, ROLE_MAIN_MODERATOR) >= 0;
        var isTargetUserOrLower = authorityComparator.compare(target.getUserAccount(), ROLE_USER) <= 0;
        boolean enoughAuthority = isAskerMainModeratorOrOver && isTargetUserOrLower;
        return target.subscriptionsAccessLevel == EVERYBODY || checksSelf || enoughAuthority;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyInfo {
        final private UserAccount userAccount;
        final private PrivacyPolicy.SubscriptionsAccessLevel subscriptionsAccessLevel;
    }
}
