package ru.combuddy.backend.controllers.contact.service.interfaces;

import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

public interface PostUserContactService {
    /**
     * @throws IllegalArgumentException if user contact does not exist
     */
    List<PostUserContact> getFromContacts(List<BaseContactInfo> postUserContacts,
                                          Post post,
                                          String creatorUsername)
            throws IllegalArgumentException;

    List<BaseContactInfo> getBaseContacts(List<PostUserContact> postContacts);
}
