package ru.combuddy.backend.security.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.security.entities.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
