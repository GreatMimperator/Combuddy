package ru.combuddy.backend.security.verifiers.users.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.entities.user.PrivacyPolicy.RegisteredDateAccessLevel.EVERYBODY;
import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_MODERATOR;
import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_USER;

@Component
@AllArgsConstructor
public class RegisteredDateAccessVerifier implements PermissionVerifier<RegisteredDateAccessVerifier.VerifyInfo> {

    public final Role.RoleName.AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.userAccount);
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        var isTargetUserOrLower = authorityComparator.compare(target.getUserAccount(), ROLE_USER) <= 0;
        boolean enoughAuthority = isAskerModeratorOrOver && isTargetUserOrLower;
        return target.registeredDateAccessLevel == EVERYBODY || checksSelf || enoughAuthority;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyInfo {
        final private UserAccount userAccount;
        final private PrivacyPolicy.RegisteredDateAccessLevel registeredDateAccessLevel;
    }
}
