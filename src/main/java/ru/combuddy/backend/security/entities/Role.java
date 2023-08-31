package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.user.InvalidRoleNameException;

import java.text.MessageFormat;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    private List<UserAccount> users;

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
                    MessageFormat.format("Role name {0} does not exist as enum value",
                            roleNameAsString));
        }
    }

}
