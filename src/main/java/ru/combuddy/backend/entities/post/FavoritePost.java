package ru.combuddy.backend.entities.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "subscriber_id"}))
@Data
@EqualsAndHashCode(of = "id")
public class FavoritePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private UserAccount subscriber;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar savedTime = Calendar.getInstance();
}
