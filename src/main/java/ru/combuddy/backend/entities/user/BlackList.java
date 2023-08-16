package ru.combuddy.backend.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"defended_id", "aggressor_id"}))
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "aggressor_id", nullable = false)
    private UserAccount aggressor;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "defended_id", nullable = false)
    private UserAccount defended;
}
