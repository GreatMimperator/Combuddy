package ru.combuddy.backend.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"defended_id", "aggressor_id"}))
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
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
