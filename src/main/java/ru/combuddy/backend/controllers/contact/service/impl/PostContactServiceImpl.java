package ru.combuddy.backend.controllers.contact.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.service.interfaces.PostContactService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.repositories.contact.post.PostContactRepository;

import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PostContactServiceImpl implements PostContactService {

    private final UserAccountService userAccountService;
    private final PostContactRepository postContactRepository;


    @Override
    public List<PostContact> getFromContacts(List<BaseContactInfo> postContacts, Post post) {
        return postContacts.stream()
                .map(contact -> contact.fillPostContact(post))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<BaseContactInfo> getBaseContacts(List<PostContact> contacts) {
        return contacts.stream()
                .map(BaseContactInfo::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
