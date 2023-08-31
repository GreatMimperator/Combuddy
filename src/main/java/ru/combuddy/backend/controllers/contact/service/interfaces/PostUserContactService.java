package ru.combuddy.backend.controllers.contact.service.interfaces;

import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.contact.NotFoundUserContactException;

import java.util.List;

public interface PostUserContactService {
    /**
     * @return modifiable list
     */
    List<PostUserContact> getFromContacts(List<BaseContactInfo> postUserContacts,
                                          Post post,
                                          String creatorUsername)
            throws NotFoundUserContactException;

    /**
     * @return modifiable list
     */
    List<BaseContactInfo> getBaseContacts(List<PostUserContact> postContacts);
}
