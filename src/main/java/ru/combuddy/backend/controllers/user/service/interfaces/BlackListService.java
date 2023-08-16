package ru.combuddy.backend.controllers.user.service.interfaces;

import java.util.List;
import java.util.Optional;

public interface BlackListService {
    Optional<List<String>> getAggressorUsernames(String defendedUsername);

    /**
     * @return false if any of user do not exist, true if blacklist contains now
     */
    boolean add(String aggressorUsername, String defendedUsername);

    /**
     * @return false if any of user do not exist, true if blacklist does not contain now
     */
    boolean remove(String aggressorUsername, String defendedUsername);
}
