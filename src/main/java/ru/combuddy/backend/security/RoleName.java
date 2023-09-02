package ru.combuddy.backend.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.combuddy.backend.exceptions.user.InvalidRoleNameException;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum RoleName implements Comparable<RoleName> {
    ROLE_USER(0),
    ROLE_MODERATOR(1),
    ROLE_MAIN_MODERATOR(2);

    private final int authorityOrderMarker;


    /**
     * Converts using {@link #name()} and ignoring case in equal check
     *
     * @throws InvalidRoleNameException if role name does not exist
     */
    public static RoleName convertToRoleName(String roleNameAsString) throws InvalidRoleNameException {
        for (var roleName : RoleName.values()) {
            if (roleName.name().equalsIgnoreCase(roleNameAsString)) {
                return roleName;
            }
        }
        throw new InvalidRoleNameException(
                MessageFormat.format("RoleName name {0} does not exist as enum value",
                        roleNameAsString));
    }

    /**
     * @return {@link #name()}
     */
    public String getName() {
        return name();
    }
}
