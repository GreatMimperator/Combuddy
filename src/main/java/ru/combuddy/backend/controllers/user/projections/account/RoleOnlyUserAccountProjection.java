package ru.combuddy.backend.controllers.user.projections.account;

import ru.combuddy.backend.security.RoleName;

public interface RoleOnlyUserAccountProjection {
    RoleName getRole();
}
