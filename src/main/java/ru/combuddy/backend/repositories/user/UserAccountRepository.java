package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.account.FrozenOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.projections.account.RoleOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.security.entities.Role;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    int deleteByUsername(String username);

    List<UsernameOnlyUserAccountProjection> findByUsernameStartingWith(String usernameBeginPart);

    Optional<FrozenOnlyUserAccountProjection> findFrozenByUsername(String username);

    Optional<RoleOnlyUserAccountProjection> findRoleByUsername(String username);
}
