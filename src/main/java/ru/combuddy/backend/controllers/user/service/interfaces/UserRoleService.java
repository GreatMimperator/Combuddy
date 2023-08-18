package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.security.entities.Role;

import java.util.List;

public interface UserRoleService {
    List<Role> getRoles(String username);
}
