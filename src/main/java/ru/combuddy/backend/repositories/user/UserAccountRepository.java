package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import ru.combuddy.backend.controllers.user.projections.account.FrozenOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    int deleteByUsername(String username);

    List<UsernameOnlyUserAccountProjection> findByUsernameStartingWith(String usernameBeginPart, Pageable pageable);

    Optional<FrozenOnlyUserAccountProjection> findFrozenByUsername(String username);
}
