package ru.combuddy.backend.security.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.security.entities.WorkingRefreshToken;

import java.util.Optional;

public interface WorkingRefreshTokenRepository extends CrudRepository<WorkingRefreshToken, Long> {
    Optional<WorkingRefreshToken> findByOwnerUsername(String ownerUsername);
    void deleteByOwnerUsername(String ownerUsername);
}
