package ru.combuddy.backend.controllers.user.projections.account;

import ru.combuddy.backend.security.entities.Role;

public interface RoleOnlyUserAccountProjection {
    Role getRole();
}
