package ru.combuddy.backend.controllers.user.projections.roles;

import ru.combuddy.backend.security.entities.Role;

public interface RoleOnlyUserRoleProjection {
    Role getRole();
}
