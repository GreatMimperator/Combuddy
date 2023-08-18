package ru.combuddy.backend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.combuddy.backend.security.entities.Role;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleUtil {

    public static Set<String> getStringAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static Set<String> getStringAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

}
