package ru.combuddy.backend.entities.post.tag;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tag_id", "filterType"}))
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserHomeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private FilterType filterType;

    public UserHomeTag(Long id, UserAccount user, Tag tag, FilterType filterType) {
        this.id = id;
        this.user = user;
        this.tag = tag;
        this.filterType = filterType;
    }

    public enum FilterType { INCLUDING, EXCLUDING }
}
