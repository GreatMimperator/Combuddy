package ru.combuddy.backend.entities.contact.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.entities.post.Post;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_contact_id"}))
@Data
@EqualsAndHashCode(of = "id")
public class PostUserContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_contact_id", nullable = false)
    private UserContact userContact;
}
