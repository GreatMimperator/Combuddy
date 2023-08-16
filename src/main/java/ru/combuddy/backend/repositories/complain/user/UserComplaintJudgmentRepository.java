package ru.combuddy.backend.repositories.complain.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.complain.user.UserComplaintJudgment;

public interface UserComplaintJudgmentRepository extends CrudRepository<UserComplaintJudgment, Long> {
}
