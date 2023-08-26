package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.projections.blacklist.BlackListAggressorUsernameProjection;
import ru.combuddy.backend.entities.user.BlackList;

import java.util.List;
import java.util.Optional;

public interface BlackListRepository extends CrudRepository<BlackList, Long> {
    List<BlackListAggressorUsernameProjection> findAggressorUsernamesByDefendedUsername(
            String defendedUsername);

    boolean existsByAggressorIdAndDefendedId(
            Long aggressorId,
            Long defendedId);

    int deleteByAggressorUsernameAndDefendedUsername(
            String aggressorUsername,
            String defendedUsername);

    Optional<BlackList> findByAggressorUsernameAndDefendedUsername(
            String aggressorUsername,
            String defendedUsername);
}
