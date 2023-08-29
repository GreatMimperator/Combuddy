package ru.combuddy.backend.entities.contact;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.security.entities.Role;

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
         * Converts using name() call of ContactType and ignoring case in equal check
         *
         * @throws NotExistsException if has not contact type with this name
         */
        public static ContactType convertToContactType(String contactTypeAsString) throws NotExistsException {
            for (var contactType : ContactType.values()) {
                if (contactType.name().equalsIgnoreCase(contactTypeAsString)) {
                    return contactType;
                }
            }
            throw new NotExistsException(
                    MessageFormat.format("Contact type {0} does not exist as enum value",
                            contactTypeAsString),
                    contactTypeAsString);

        }
    }
}
