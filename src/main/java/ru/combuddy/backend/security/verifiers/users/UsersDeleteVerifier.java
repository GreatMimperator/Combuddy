package ru.combuddy.backend.security.verifiers.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.RoleName.ROLE_MAIN_MODERATOR;
import static ru.combuddy.backend.security.RoleName.ROLE_USER;

@Component
@AllArgsConstructor
public class UsersDeleteVerifier implements PermissionVerifier<UserAccount> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, UserAccount target) {
        var isAskerMainModeratorOrOver = authorityComparator.compare(asker, ROLE_MAIN_MODERATOR) >= 0;
        var isTargetUserOrLower = authorityComparator.compare(target, ROLE_USER) <= 0 ;
        return isTargetUserOrLower && isAskerMainModeratorOrOver;
    }
}
