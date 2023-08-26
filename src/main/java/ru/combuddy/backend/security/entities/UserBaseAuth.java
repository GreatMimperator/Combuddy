package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

@Entity
@Data
@EqualsAndHashCode(of = {"id", "encryptedPassword"})
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserAccount userAccount;

    @Column(nullable = false)
    private String encryptedPassword;
}
