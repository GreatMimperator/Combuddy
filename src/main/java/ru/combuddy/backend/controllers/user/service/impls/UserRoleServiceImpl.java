package ru.combuddy.backend.controllers.user.service.impls;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.projections.roles.RoleOnlyUserRoleProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserRoleService;
import ru.combuddy.backend.repositories.user.UserRoleRepository;
import ru.combuddy.backend.security.entities.Role;

import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public List<Role> getRoles(String username) {
        return userRoleRepository.getRolesByUserAccountUsername(username).stream()
                .map(RoleOnlyUserRoleProjection::getRole)
                .toList();
    }
}
