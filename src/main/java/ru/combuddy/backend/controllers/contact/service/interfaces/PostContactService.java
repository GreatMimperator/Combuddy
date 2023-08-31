package ru.combuddy.backend.controllers.contact.service.interfaces;

import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

@Service
public interface PostContactService {
    /**
     * @return modifiable list
     */
    List<PostContact> getFromContacts(List<BaseContactInfo> postContacts, Post post);
    /**
     * @return modifiable list
     */
    List<BaseContactInfo> getBaseContacts(List<PostContact> contacts);
}
