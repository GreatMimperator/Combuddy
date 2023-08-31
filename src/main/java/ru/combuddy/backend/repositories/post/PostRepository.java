package ru.combuddy.backend.repositories.post;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.combuddy.backend.entities.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
