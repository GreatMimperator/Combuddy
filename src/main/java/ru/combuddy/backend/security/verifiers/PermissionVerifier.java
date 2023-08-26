package ru.combuddy.backend.security.verifiers;

import ru.combuddy.backend.entities.user.UserAccount;

@FunctionalInterface
public interface PermissionVerifier<T> {
    /**
     * Both ({@link UserAccount} and another) objects should return actual data on getters calls
     */
    boolean verify(UserAccount asker, T target);
}
