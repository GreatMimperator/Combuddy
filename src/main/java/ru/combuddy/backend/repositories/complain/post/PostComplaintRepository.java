package ru.combuddy.backend.repositories.complain.post;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.complain.post.PostComplaint;

public interface PostComplaintRepository extends CrudRepository<PostComplaint, Long> {
}
