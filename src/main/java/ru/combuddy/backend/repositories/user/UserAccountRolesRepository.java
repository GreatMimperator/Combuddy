package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.roles.RoleOnlyUserAccountRolesProjection;
import ru.combuddy.backend.entities.user.UserAccountRoles;

import java.util.List;

public interface UserAccountRolesRepository extends CrudRepository<UserAccountRoles, Long> {
    List<RoleOnlyUserAccountRolesProjection> getRolesByUserAccountUsername(String username);
}
