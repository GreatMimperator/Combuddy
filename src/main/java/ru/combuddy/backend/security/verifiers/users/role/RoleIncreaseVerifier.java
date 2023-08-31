package ru.combuddy.backend.security.verifiers.users.role;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.entities.Role.RoleName.*;

@Component
@AllArgsConstructor
public class RoleIncreaseVerifier implements PermissionVerifier<RoleIncreaseVerifier.VerifyInfo> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.userAccount);
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        var isAskerOverTargetRole = authorityComparator.compare(asker, target.issuedRole) > 0;
        boolean enoughAuthority = isAskerModeratorOrOver && isAskerOverTargetRole;
        var isIncreaseOrEq = authorityComparator.compare(target.issuedRole, target.userAccount.getRole().getName()) >= 0;
        return !checksSelf && enoughAuthority && isIncreaseOrEq;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyInfo {
        final private UserAccount userAccount;
        final private Role.RoleName issuedRole;
    }
}
