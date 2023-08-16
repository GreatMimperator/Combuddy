package ru.combuddy.backend.entities.tag;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.combuddy.backend.entities.user.UserAccount;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tag_id"}))
public class UserHomeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private FilterType filterType;

    public enum FilterType { INCLUDING, EXCLUDING }
}
