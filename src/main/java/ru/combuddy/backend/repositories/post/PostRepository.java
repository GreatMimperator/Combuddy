package ru.combuddy.backend.repositories.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.post.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
