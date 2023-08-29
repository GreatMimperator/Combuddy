package ru.combuddy.backend.repositories.contact.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.entities.contact.user.UserContact;

import java.util.List;
import java.util.Optional;

public interface UserContactRepository extends CrudRepository<UserContact, Long> {
    boolean existsByOwnerUsernameAndValue(String ownerUsername, String value);

    int deleteByOwnerUsernameAndContactTypeAndValue(String ownerUsername, ContactType contactType, String contact);

    Optional<UserContact> findByOwnerUsernameAndContactTypeAndValue(String ownerUsername, ContactType contactType, String contact);

    List<UserContact> findByOwnerUsername(String ownerUsername);
}
