package ru.combuddy.backend.controllers.contact.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.post.Post;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"contactType", "value"})
public class BaseContactInfo {
    @NotNull
    @JsonAlias("type")
    private ContactType contactType;
    @NotNull
    private String value;

    public BaseContactInfo(BaseContact baseContact) {
        setContactType(baseContact.getContactType());
        setValue(baseContact.getValue());
    }

    public PostContact fillPostContact(Post post) {
        return new PostContact(null, post, contactType, value);
    }
}
