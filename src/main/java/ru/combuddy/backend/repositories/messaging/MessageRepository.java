package ru.combuddy.backend.repositories.messaging;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.messaging.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
