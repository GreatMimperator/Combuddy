package ru.combuddy.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.user.InvalidRoleNameException;
import ru.combuddy.backend.security.entities.Role.RoleName;

import java.util.Comparator;

@Component
public class AuthorityComparator implements Comparator<RoleName> {
    @Override
    public int compare(RoleName first, RoleName second) {
        return Integer.compare(
                first.getAuthorityOrderMarker(),
                second.getAuthorityOrderMarker());
    }

    public boolean overOrEqual(RoleName first, RoleName second) {
        return compare(first, second) >= 0;
    }

    public boolean overOrEqual(UserAccount first, RoleName secondRoleName) {
        return compare(first, secondRoleName) >= 0;
    }
    public int compare(UserAccount first, RoleName secondRoleName) {
        var firstRoleName = first.getRole().getName();
        return compare(firstRoleName, secondRoleName);
    }

    public int compare(UserAccount first, UserAccount second) {
        var firstRoleName = first.getRole().getName();
        var secondRoleName = second.getRole().getName();
        return compare(firstRoleName, secondRoleName);
    }

    /**
     * @throws InvalidRoleNameException if role name does not exist
     */
    public int compare(Authentication firstAuthentication, RoleName second) {
        var firstRoleName = convertAuthenticationToRoleName(firstAuthentication);
        return compare(firstRoleName, second);
    }

    /**
     * @throws InvalidRoleNameException if role name does not exist
     */
    public boolean overOrEqual(Authentication firstAuthentication, RoleName second) {
        return compare(firstAuthentication, second) >= 0;
    }

    /**
     * @throws InvalidRoleNameException if role name does not exist
     */
    public static RoleName convertAuthenticationToRoleName(Authentication authentication)
            throws InvalidRoleNameException {
        var roleAsString = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        return RoleName.convertToRoleName(roleAsString);
    }
}
