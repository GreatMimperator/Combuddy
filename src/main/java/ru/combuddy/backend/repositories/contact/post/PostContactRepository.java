package ru.combuddy.backend.repositories.contact.post;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.contact.post.PostContact;

public interface PostContactRepository extends CrudRepository<PostContact, Long> {
}
