package ru.combuddy.backend.controllers.contact.service.interfaces;

import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.exceptions.contact.InvalidContactValueException;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;
import ru.combuddy.backend.exceptions.contact.UserContactAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.util.List;
import java.util.Optional;

public interface UserContactService {
    /**
     * Checks {@code contact} with {@code contactType} regex <br>
     */
    void check(ContactType contactType, String contact) throws InvalidContactValueException;

    /**
     * You should put {@code contact} already checked with {@code contactType} regex <br>
     * If you want exception to be thrown on check fail - use {@link #add(String, ContactType, String)}
     */
    UserContact addChecked(String ownerUsername,
                           ContactType contactType,
                           String contact)
            throws UserNotExistsException,
            UserContactAlreadyExistsException;

    /**
     * Checks with {@link #check(ContactType, String)} before adding <br>
     * If you have already provided this check - use {@link #addChecked(String, ContactType, String)}
     */
    UserContact add(String ownerUsername,
                    ContactType contactType,
                    String contact)
                    throws UserNotExistsException,
                    UserContactAlreadyExistsException;

    boolean exists(String ownerUsername,
                   ContactType contactType,
                   String contact);

    boolean delete(String ownerUsername,
                   ContactType contactType,
                   String contact);

    Optional<UserContact> find(String ownerUsername,
                               ContactType contactType,
                               String contact);

    UserContact get(String ownerUsername,
                    ContactType contactType,
                    String contact)
            throws NotFoundUserContactException;

    List<UserContact> getAll(String username);

    void putChecked(String ownerUsername,
                    ContactType contactType,
                    String contact);

    /**
     * @return modifiable list
     */
    List<BaseContactInfo> toBaseContacts(List<UserContact> userContacts);
}
