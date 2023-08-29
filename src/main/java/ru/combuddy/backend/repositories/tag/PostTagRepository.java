package ru.combuddy.backend.repositories.tag;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.post.tag.PostTag;

public interface PostTagRepository extends CrudRepository<PostTag, Long> {
}
