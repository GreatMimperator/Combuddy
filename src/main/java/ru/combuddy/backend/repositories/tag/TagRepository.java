package ru.combuddy.backend.repositories.tag;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.tag.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
}
