package ru.combuddy.backend.entities.messaging;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Data
@Entity
public class PublicMessage {
    public static final int MIN_TEXT_LENGTH = 1;
    public static final int MAX_TEXT_LENGTH = 250;

    public static final int MAX_PICTURE_MB_SIZE = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserAccount sender;

    @ManyToOne
    @JoinColumn(name = "message_root_id")
    private PublicMessage rootMessage;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar date = Calendar.getInstance();

    @NotNull
    @Size(min = MIN_TEXT_LENGTH, max = MAX_TEXT_LENGTH)
    @Column(length = MAX_TEXT_LENGTH, nullable = false)
    private String text;

    @Size(max = MAX_PICTURE_MB_SIZE * 1024 * 1024)
    @Lob
    private byte[] picture;
}
