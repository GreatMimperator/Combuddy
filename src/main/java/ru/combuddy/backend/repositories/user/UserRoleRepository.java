package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.roles.RoleOnlyUserRoleProjection;
import ru.combuddy.backend.entities.user.UserRole;

import java.util.List;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    List<RoleOnlyUserRoleProjection> getRolesByUserAccountUsername(String username);
}
