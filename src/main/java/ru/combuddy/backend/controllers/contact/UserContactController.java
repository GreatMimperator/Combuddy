package ru.combuddy.backend.controllers.contact;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.exceptions.NotExistsException;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.matches;
import static ru.combuddy.backend.entities.contact.BaseContact.ContactType.convertToContactType;

@RestController
@RequestMapping("/api/contact")
@AllArgsConstructor
public class UserContactController {

    private final UserContactService userContactService;

    @PutMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void add(@RequestParam("contactType") String contactTypeAsString,
                    @RequestParam String contact,
                    Authentication authentication) {
        ContactType contactType;
        try {
            contactType = convertToContactType(contactTypeAsString);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageFormat.format("Contact type {0} does not exist",
                            contactTypeAsString));
        }
        if (!Pattern.matches(contactType.getValidationRegex(), contact)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageFormat.format("Contact {0} does not satisfy the contact type regex {1}",
                            contact,
                            contactTypeAsString));
        }
        var username = authentication.getName();
        if (userContactService.exists(username, contact)) {
            return;
        }
        userContactService.addChecked(username, contactType, contact);
    }

    @DeleteMapping("/remove") // todo: all remove to delete
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestParam("contactType") String contactTypeAsString,
                       @RequestParam String contact,
                       Authentication authentication) {
        ContactType contactType;
        try {
            contactType = convertToContactType(contactTypeAsString);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageFormat.format("Contact type {0} does not exist",
                            contactTypeAsString));
        }
        var username = authentication.getName();
        userContactService.delete(username, contactType, contact);
    }

    @GetMapping("/list")
    public ContactList list(Authentication authentication) {
        var username = authentication.getName();
        var baseContacts = userContactService.getAll(username).stream()
                .map(BaseContactInfo::new)
                .toList();
        return new ContactList(baseContacts);
    }
}
