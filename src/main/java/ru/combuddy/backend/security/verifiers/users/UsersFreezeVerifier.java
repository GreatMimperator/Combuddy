package ru.combuddy.backend.security.verifiers.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class UsersFreezeVerifier implements PermissionVerifier<UserAccount> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, UserAccount target) {
        var isAskerAuthorityOverTarget = authorityComparator.compare(asker, target) > 0;
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        return isAskerAuthorityOverTarget && isAskerModeratorOrOver;
    }
}
