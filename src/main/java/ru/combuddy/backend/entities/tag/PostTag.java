package ru.combuddy.backend.entities.tag;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.post.Post;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"}))
@Data
@EqualsAndHashCode(of = "id")
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

}
