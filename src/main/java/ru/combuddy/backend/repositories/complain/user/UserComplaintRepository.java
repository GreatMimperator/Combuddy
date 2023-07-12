package ru.combuddy.backend.repositories.complain.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.complain.user.UserComplaint;

public interface UserComplaintRepository extends CrudRepository<UserComplaint, Long> {
}
