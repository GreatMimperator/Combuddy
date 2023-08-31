package ru.combuddy.backend.repositories.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.combuddy.backend.controllers.post.projections.TagNameProjection;
import ru.combuddy.backend.entities.post.tag.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);

    Optional<Tag> findByName(String name);

    List<TagNameProjection> findNamesByNameStartingWith(String nameBeginPart);

    int deleteByName(String name);

    List<TagNameProjection> findAllNamesBy();
}
