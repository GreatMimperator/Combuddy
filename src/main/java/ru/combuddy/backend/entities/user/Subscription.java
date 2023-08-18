package ru.combuddy.backend.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"subscriber_id", "poster_id"}))
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private UserAccount subscriber;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "poster_id", nullable = false)
    private UserAccount poster;
}
