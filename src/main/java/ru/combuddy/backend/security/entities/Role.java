package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;

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

        @Component
        public static class AuthorityComparator implements Comparator<RoleName> {
            @Override
            public int compare(RoleName first, RoleName second) {
                return Integer.compare(
                        first.getAuthorityOrderMarker(),
                        second.getAuthorityOrderMarker());
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
        }



        /**
         * Converts using name field of RoleName and ignoring case in equal check
         *
         * @throws NotExistsException if has not role with this name
         */
        public static Role.RoleName convertToRoleName(String roleNameAsString) throws NotExistsException {
            for (var roleName : RoleName.values()) {
                if (roleName.name().equalsIgnoreCase(roleNameAsString)) {
                    return roleName;
                }
            }
            throw new NotExistsException(
                    MessageFormat.format("Role name {0} does not exist as enum value",
                            roleNameAsString),
                    roleNameAsString);
        }
    }
}
