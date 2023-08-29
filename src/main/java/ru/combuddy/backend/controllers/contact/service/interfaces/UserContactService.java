package ru.combuddy.backend.controllers.contact.service.interfaces;

import org.springframework.web.util.pattern.PatternParseException;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.RegexValidationException;

import java.util.List;
import java.util.Optional;

public interface UserContactService {
    /**
     * You should put {@code contact} already checked with {@code contactType} regex <br>
     * If you want exception to be thrown on check fail - use {@link #add(String, ContactType, String)}
     *
     * @throws NotExistsException if user with this {@code ownerUsername} does not exist
     */
    UserContact addChecked(String ownerUsername,
                                  ContactType contactType,
                                  String contact)
            throws NotExistsException;

    /**
     * Checks {@code contact} with {@code contactType} regex <br>
     * If you have already provided this check - use {@link #addChecked(String, ContactType, String)}
     *
     * @throws NotExistsException if user with this {@code ownerUsername} does not exist
     * @throws RegexValidationException if {@code contact} failed {@code contactType} regex check
     */
    UserContact add(String ownerUsername,
                    ContactType contactType,
                    String contact)
            throws NotExistsException, RegexValidationException;

    boolean exists(String ownerUsername, String contact);

    boolean delete(String ownerUsername, ContactType contactType, String contact);

    Optional<UserContact> find(String ownerUsername, ContactType contactType, String contact);

    List<UserContact> getAll(String username);
}
