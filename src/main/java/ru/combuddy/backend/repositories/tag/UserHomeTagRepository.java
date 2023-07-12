package ru.combuddy.backend.repositories.tag;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.tag.UserHomeTag;

public interface UserHomeTagRepository extends CrudRepository<UserHomeTag, Long> {
}
