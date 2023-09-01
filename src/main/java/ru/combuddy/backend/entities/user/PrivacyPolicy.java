package ru.combuddy.backend.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = @Index(columnList = "user_id"))
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PrivacyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    private SubscriptionsAccessLevel subscriptionsAccessLevel;

    @Enumerated(EnumType.STRING)
    private RegisteredDateAccessLevel registeredDateAccessLevel;

    public PrivacyPolicy(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public enum SubscriptionsAccessLevel { // todo: add changing
        NOBODY, EVERYBODY
    }

    public enum RegisteredDateAccessLevel {
        NOBODY, EVERYBODY
    }
}
