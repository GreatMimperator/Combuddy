package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class WorkingRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = UserAccount.MAX_USERNAME_LENGTH)
    private String ownerUsername;

    @Column(unique = true, nullable = false)
    private String jwtId;
}
