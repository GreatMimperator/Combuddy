package ru.combuddy.backend.repositories.messaging;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.messaging.Dialog;

public interface DialogRepository extends CrudRepository<Dialog, Long> {
}
