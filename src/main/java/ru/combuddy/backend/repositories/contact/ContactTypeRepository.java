package ru.combuddy.backend.repositories.contact;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.contact.ContactType;

public interface ContactTypeRepository extends CrudRepository<ContactType, Long> {
}
