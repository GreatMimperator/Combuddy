package ru.combuddy.backend.entities.messaging;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Data
@Entity
public class Message {
    public static final int MIN_TEXT_LENGTH = 1;
    public static final int MAX_TEXT_LENGTH = 1000;

    public static final int MAX_PICTURE_MB_SIZE = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

    @NotNull
    @Column(nullable = false)
    private Boolean isSenderFirst = false;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar date = Calendar.getInstance();

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar changed_date = null;

    @ManyToOne
    @JoinColumn(name = "message_reply_to_id")
    private PublicMessage messageReplyTo;

    @NotNull
    @Size(min = MIN_TEXT_LENGTH, max = MAX_TEXT_LENGTH)
    @Column(length = MAX_TEXT_LENGTH, nullable = false)
    private String text;

    @Size(max = MAX_PICTURE_MB_SIZE * 1024 * 1024)
    @Lob
    private byte[] picture;
}
