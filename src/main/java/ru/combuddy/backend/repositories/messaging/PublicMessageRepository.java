package ru.combuddy.backend.repositories.messaging;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.messaging.PublicMessage;

public interface PublicMessageRepository extends CrudRepository<PublicMessage, Long> {
}
