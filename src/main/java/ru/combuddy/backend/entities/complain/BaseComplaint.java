package ru.combuddy.backend.entities.complain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Data
@MappedSuperclass
public class BaseComplaint {
    public static final int MAX_TEXT_LENGTH = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="informer_id", nullable = false)
    private UserAccount informer;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar date = Calendar.getInstance();

    @NotNull
    @Column(length = MAX_TEXT_LENGTH, nullable = false)
    private String text;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private State state;

    public enum State { PROCESSED, BANNED, FAKE }
}
