package ru.combuddy.backend.controllers.contact.service.interfaces;

import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

public interface PostContactService {
    List<PostContact> getFromContacts(List<BaseContactInfo> postContacts, Post post);

    List<BaseContactInfo> getBaseContacts(List<PostContact> postContacts);
}
