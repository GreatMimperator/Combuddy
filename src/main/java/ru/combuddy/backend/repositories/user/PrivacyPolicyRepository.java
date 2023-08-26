package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.entities.user.PrivacyPolicy;

import java.util.Optional;

public interface PrivacyPolicyRepository extends CrudRepository<PrivacyPolicy, Long> {
    Optional<PrivacyPolicy> findByUserAccountId(Long userAccountId);
}
