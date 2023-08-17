package ru.combuddy.backend.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // todo: relation should contain date of issue of the role and whom it was issued.
    //  Many to many is not okay here
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<UserAccount> userAccounts;
}
