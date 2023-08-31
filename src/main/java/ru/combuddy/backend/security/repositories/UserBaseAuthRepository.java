package ru.combuddy.backend.security.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.security.entities.UserBaseAuth;

public interface UserBaseAuthRepository extends CrudRepository<UserBaseAuth, Long> {
    boolean existsByUserAccountId(Long userAccountId);
}
