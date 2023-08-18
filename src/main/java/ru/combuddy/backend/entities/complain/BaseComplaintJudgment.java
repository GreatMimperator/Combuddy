package ru.combuddy.backend.entities.complain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.user.UserAccount;

@MappedSuperclass
@Data
@EqualsAndHashCode(of = "id")
public class BaseComplaintJudgment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="judge_id")
    private UserAccount judge;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private State state;

    public enum State { BANNED, FAKE }
}
