package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;

import java.util.List;
import java.util.Optional;

public interface BlackListService {
    /**
     * @throws ShouldNotBeEqualException if usernames are equal
     * @throws NotExistsException if any of user do not exist
     */
    void add(String aggressorUsername, String defendedUsername) throws ShouldNotBeEqualException, NotExistsException;

    boolean delete(String aggressorUsername, String defendedUsername);

    List<String> getAggressorUsernames(String defendedUsername);

    Optional<BlackList> findRecord(String aggressorUsername, String defendedUsername);
}
