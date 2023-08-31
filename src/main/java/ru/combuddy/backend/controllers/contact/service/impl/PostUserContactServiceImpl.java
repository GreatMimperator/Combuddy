package ru.combuddy.backend.controllers.contact.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostUserContactService;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PostUserContactServiceImpl implements PostUserContactService {

    private final UserContactService userContactService;

    @Override
    public List<PostUserContact> getFromContacts(List<BaseContactInfo> postUserContacts,
                                                 Post post,
                                                 String creatorUsername)
            throws NotFoundUserContactException {
        return postUserContacts.stream()
                .map(contact ->
                        userContactService.get(
                                creatorUsername,
                                contact.getContactType(),
                                contact.getValue()))
                .map(userContact -> new PostUserContact(null, post, userContact))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<BaseContactInfo> getBaseContacts(List<PostUserContact> contacts) {
        return contacts.stream()
                .map(PostUserContact::getUserContact)
                .map(BaseContactInfo::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
