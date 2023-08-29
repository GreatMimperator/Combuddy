package ru.combuddy.backend.entities.post.tag;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tag_id", "filterType"})) // todo: why adding filterType makes test of TagControllerTest work because save?
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
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

    public UserHomeTag(Long id, UserAccount user, Tag tag, FilterType filterType) {
        this.id = id;
        this.user = user;
        this.tag = tag;
        this.filterType = filterType;
    }

    public enum FilterType { INCLUDING, EXCLUDING }
}
