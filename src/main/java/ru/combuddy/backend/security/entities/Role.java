package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserRole;

import java.text.MessageFormat;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "name")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    private Set<UserRole> userRoles;

    /**
     * @throws IllegalArgumentException if hierarchy markers do not have the same length
     */
    public static boolean isAboveInHierarchy(boolean[] firstHierarchyMarkers, boolean[] secondHierarchyMarkers)
            throws IllegalArgumentException {
        if (firstHierarchyMarkers.length != secondHierarchyMarkers.length) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Hierarchy markers do not have the same length: {0} != {1}",
                            firstHierarchyMarkers.length,
                            secondHierarchyMarkers.length));
        }
        for (var i = firstHierarchyMarkers.length - 1; i >= 0; i--) {
            // any of them is above, or they are the same level
            if (firstHierarchyMarkers[i] || secondHierarchyMarkers[i]) {
                return firstHierarchyMarkers[i] && !secondHierarchyMarkers[i];
            }
        }
        return false;
    }
}
