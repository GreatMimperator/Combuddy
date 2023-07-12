package ru.combuddy.backend.entities.contact;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@MappedSuperclass
public class BaseContact {
    public static final int MIN_VALUE_LENGTH = 3;
    public static final int MAX_VALUE_LENGTH = 125;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "contact_type_id", nullable = false)
    private ContactType contactType;

    @NotNull
    @Size(min = MIN_VALUE_LENGTH, max = MAX_VALUE_LENGTH)
    @Column(length = MAX_VALUE_LENGTH, nullable = false)
    private String value;
}
