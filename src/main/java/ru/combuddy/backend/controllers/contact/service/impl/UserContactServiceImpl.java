package ru.combuddy.backend.controllers.contact.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.exceptions.contact.InvalidContactValueException;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;
import ru.combuddy.backend.exceptions.contact.UserContactAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.contact.user.UserContactRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserContactServiceImpl implements UserContactService {

    private UserContactRepository userContactRepository;
    private UserAccountService userAccountService;

    @Override
    public void check(ContactType contactType, String contact) throws InvalidContactValueException {
        if (!Pattern.matches(contactType.getValidationRegex(), contact)) {
            throw new InvalidContactValueException("Contact does not match its contact type regex");
        }
    }

    @Override
    public UserContact addChecked(String ownerUsername,
                                  ContactType contactType,
                                  String contact)
            throws UserNotExistsException,
            UserContactAlreadyExistsException {
        var owner = userAccountService.getByUsername(ownerUsername);
        if (this.exists(ownerUsername, contactType, contact)) {
            throw new UserContactAlreadyExistsException("User contact already exists");
        }
        var userContact = new UserContact(null, owner, contactType, contact);
        return userContactRepository.save(userContact);
    }


    @Override
    public UserContact add(String ownerUsername,
                           ContactType contactType,
                           String contact)
            throws UserNotExistsException,
            UserContactAlreadyExistsException {
        this.check(contactType, contact);
        return addChecked(ownerUsername, contactType, contact);
    }

    @Override
    public boolean exists(String ownerUsername,
                          ContactType contactType,
                          String contact) {
        return userContactRepository
                .existsByOwnerUsernameAndContactTypeAndValue(
                        ownerUsername,
                        contactType,
                        contact);
    }

    @Override
    public boolean delete(String ownerUsername,
                          ContactType contactType,
                          String contact) {
        int deleted = userContactRepository
                .deleteByOwnerUsernameAndContactTypeAndValue(
                        ownerUsername,
                        contactType,
                        contact);
        return deleted > 0;
    }

    @Override
    public Optional<UserContact> find(String ownerUsername,
                                      ContactType contactType,
                                      String contact) {
        return userContactRepository
                .findByOwnerUsernameAndContactTypeAndValue(
                        ownerUsername,
                        contactType,
                        contact);
    }

    @Override
    public UserContact get(String ownerUsername,
                           ContactType contactType,
                           String contact)
            throws NotFoundUserContactException{
        var foundUserContact = this.find(ownerUsername, contactType, contact);
        if (foundUserContact.isEmpty()) {
            throw new NotFoundUserContactException("User contact not found by username, type and value");
        }
        return foundUserContact.get();
    }

    @Override
    public List<UserContact> getAll(String username) {
        return userContactRepository.findByOwnerUsername(username);
    }

    @Override
    public void putChecked(String ownerUsername,
                           ContactType contactType,
                           String contact) {
        if (this.exists(ownerUsername, contactType, contact)) {
            return;
        }
        this.addChecked(ownerUsername, contactType, contact);
    }

    @Override
    public List<BaseContactInfo> toBaseContacts(List<UserContact> userContacts) {
        return userContacts.stream()
                .map(BaseContactInfo::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
