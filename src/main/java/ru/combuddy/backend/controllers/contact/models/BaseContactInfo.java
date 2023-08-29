package ru.combuddy.backend.controllers.contact.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.user.UserAccount;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"contactType", "contact"})
public class BaseContactInfo {
    private ContactType contactType;
    @JsonAlias("contact")
    private String value;

    public BaseContactInfo(BaseContact baseContact) {
        this.contactType = baseContact.getContactType();
        this.value = baseContact.getValue();
    }

    public PostContact fillPostContact(Post post) {
        return new PostContact(null, post, contactType, value);
    }
}
