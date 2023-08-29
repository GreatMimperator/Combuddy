package ru.combuddy.backend.controllers.contact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactList {
    List<BaseContactInfo> contacts;
}
