package ru.combuddy.backend.controllers.contact;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;
import static ru.combuddy.backend.entities.contact.BaseContact.ContactType.convertToContactType;

@RestController
@RequestMapping("/api/v1/contact")
@AllArgsConstructor
public class UserContactController {

    private final UserContactService userContactService;

    @PutMapping(value = "/put")
    @ResponseStatus(HttpStatus.CREATED)
    public void put(@RequestParam("contactType") String contactTypeAsString,
                    @RequestParam String contact,
                    Authentication authentication) {
        var creatorUsername = getUsername(authentication);
        var contactType = convertToContactType(contactTypeAsString);
        if (!Pattern.matches(contactType.getValidationRegex(), contact)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageFormat.format("Contact {0} does not satisfy the contact type regex {1}",
                            contact,
                            contactType.getValidationRegex()));
        }
        userContactService.putChecked(creatorUsername, contactType, contact);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam("contactType") String contactTypeAsString,
                       @RequestParam String contact,
                       Authentication authentication) {
        var username = getUsername(authentication);
        var contactType = convertToContactType(contactTypeAsString);
        userContactService.delete(username, contactType, contact);
    }

    @GetMapping("/list")
    public ContactList list(Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        var userContacts = userContactService.getAll(receiverUsername);
        return new ContactList(userContactService.toBaseContacts(userContacts));
    }
}
