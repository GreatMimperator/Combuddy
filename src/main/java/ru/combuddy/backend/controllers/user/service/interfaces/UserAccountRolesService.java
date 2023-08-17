package ru.combuddy.backend.controllers.user.service.interfaces;

import org.springframework.security.core.GrantedAuthority;
import ru.combuddy.backend.security.entities.Role;

import java.util.Collection;
import java.util.List;

public interface UserAccountRolesService {
    List<Role> getRoles(String username);
}
