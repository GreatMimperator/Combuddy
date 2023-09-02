package ru.combuddy.backend.entities.messaging;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"first_participant_id", "second_participant_id", "post_id"}))
@Data
@EqualsAndHashCode(of = "id")
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "first_participant_id", nullable = false)
    private UserAccount firstParticipant;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "second_participant_id", nullable = false)
    private UserAccount secondParticipant;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post topicPost;

    @NotNull
    @Column(nullable = false)
    private Boolean archived = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dialog")
    private List<Message> messages;
}
