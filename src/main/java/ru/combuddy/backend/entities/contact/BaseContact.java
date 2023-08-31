package ru.combuddy.backend.entities.contact;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.combuddy.backend.exceptions.contact.InvalidContactTypeException;

import java.text.MessageFormat;


@MappedSuperclass
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class BaseContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    @Column(nullable = false)
    private String value;

    @AllArgsConstructor
    @Getter
    public enum ContactType {
        TELEGRAM("[a-z0-9_]{5,32}"),
        X("[A-z0-9_]{1, 15}"),
        VK("[-a-zA-Z0-9_]{1,32}"),
        YOUTUBE("[-a-zA-Z0-9_]{1,64}"),
        GITHUB("[-a-zA-Z0-9_]{1,39}"),
        GITLAB("[-a-zA-Z0-9_]{1,39}"),
        MAIL("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

        private final String validationRegex;

        /**
         * Converts using {@link ContactType#name()} and ignoring case in equal check
         *
         * @throws InvalidContactTypeException if contact type does not exist (annotated with {@link ResponseStatus})
         */
        public static ContactType convertToContactType(String contactTypeAsString) throws InvalidContactTypeException {
            for (var contactType : ContactType.values()) {
                if (contactType.name().equalsIgnoreCase(contactTypeAsString)) {
                    return contactType;
                }
            }
            throw new InvalidContactTypeException(
                    MessageFormat.format("Contact type {0} does not exist as enum value",
                            contactTypeAsString));

        }
    }
}
