package ru.combuddy.backend.repositories.contact.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.contact.user.UserContact;

public interface UserContactRepository extends CrudRepository<UserContact, Long> {
}
