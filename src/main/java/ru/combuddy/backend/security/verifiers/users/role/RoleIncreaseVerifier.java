package ru.combuddy.backend.security.verifiers.users.role;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.RoleName;
import ru.combuddy.backend.security.verifiers.PermissionVerifier;

import static ru.combuddy.backend.security.RoleName.ROLE_MODERATOR;

@Component
@AllArgsConstructor
public class RoleIncreaseVerifier implements PermissionVerifier<RoleIncreaseVerifier.VerifyInfo> {

    public final AuthorityComparator authorityComparator;

    @Override
    public boolean verify(UserAccount asker, VerifyInfo target) {
        boolean checksSelf = asker.equals(target.userAccount);
        var isAskerModeratorOrOver = authorityComparator.compare(asker, ROLE_MODERATOR) >= 0;
        var isAskerOverTargetRoleName = authorityComparator.compare(asker, target.issuedRoleName) > 0;
        boolean enoughAuthority = isAskerModeratorOrOver && isAskerOverTargetRoleName;
        var isIncreaseOrEq = authorityComparator.compare(target.issuedRoleName, target.userAccount.getRoleName()) >= 0;
        return !checksSelf && enoughAuthority && isIncreaseOrEq;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyInfo {
        final private UserAccount userAccount;
        final private RoleName issuedRoleName;
    }
}
