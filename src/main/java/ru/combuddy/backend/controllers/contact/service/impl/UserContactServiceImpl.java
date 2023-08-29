package ru.combuddy.backend.controllers.contact.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.RegexValidationException;
import ru.combuddy.backend.repositories.contact.user.UserContactRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional // todo: do it for every service impl (and remove from methods)
@AllArgsConstructor
public class UserContactServiceImpl implements UserContactService {

    private final UserAccountService userAccountService;
    private final UserContactRepository userContactRepository;


    @Override
    public UserContact addChecked(String ownerUsername, ContactType contactType, String contact)
            throws NotExistsException {
        var owner = userAccountService.getByUsername(ownerUsername, "owner");
        var userContact = new UserContact(null, owner, contactType, contact);
        return userContactRepository.save(userContact);
    }

    @Override
    public UserContact add(String ownerUsername, ContactType contactType, String contact)
            throws NotExistsException, RegexValidationException {
        if (!Pattern.matches(contactType.getValidationRegex(), contact)) {
            throw new RegexValidationException("Contact does not match its contact type regex");
        }
        return addChecked(ownerUsername, contactType, contact);
    }

    @Override
    public boolean exists(String ownerUsername, String contact) {
        return userContactRepository.existsByOwnerUsernameAndValue(ownerUsername, contact);
    }

    @Override
    public boolean delete(String ownerUsername, ContactType contactType, String contact) {
        int deleted = userContactRepository.deleteByOwnerUsernameAndContactTypeAndValue(ownerUsername, contactType, contact);
        return deleted > 0;
    }

    @Override
    public Optional<UserContact> find(String ownerUsername, ContactType contactType, String contact) {
        return userContactRepository.findByOwnerUsernameAndContactTypeAndValue(ownerUsername, contactType, contact);
    }

    @Override
    public List<UserContact> getAll(String username) {
        return userContactRepository.findByOwnerUsername(username);
    }
}
