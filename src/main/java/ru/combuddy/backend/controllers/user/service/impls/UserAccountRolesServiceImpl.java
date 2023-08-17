package ru.combuddy.backend.controllers.user.service.impls;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.projections.roles.RoleOnlyUserAccountRolesProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountRolesService;
import ru.combuddy.backend.repositories.user.UserAccountRolesRepository;
import ru.combuddy.backend.security.entities.Role;

import java.util.List;

@Service
@AllArgsConstructor
public class UserAccountRolesServiceImpl implements UserAccountRolesService {

    private final UserAccountRolesRepository userAccountRolesRepository;

    @Override
    public List<Role> getRoles(String username) {
        return userAccountRolesRepository.getRolesByUserAccountUsername(username).stream()
                .map(RoleOnlyUserAccountRolesProjection::getRole)
                .toList();
    }
}
