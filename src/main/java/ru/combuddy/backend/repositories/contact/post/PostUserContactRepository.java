package ru.combuddy.backend.repositories.contact.post;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.contact.post.PostUserContact;

public interface PostUserContactRepository extends CrudRepository<PostUserContact, Long> {
}
