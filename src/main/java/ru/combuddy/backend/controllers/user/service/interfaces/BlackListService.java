package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;

import java.util.List;
import java.util.Optional;

public interface BlackListService {
    List<String> getAggressorUsernames(String defendedUsername);

    /**
     * @throws ShouldNotBeEqualException if usernames are equal
     * @throws NotExistsException if any of user do not exist
     */
    void add(String aggressorUsername, String defendedUsername) throws ShouldNotBeEqualException, NotExistsException;

    /**
     * @throws ShouldNotBeEqualException if usernames are equal
     * @throws NotExistsException if any of user do not exist
     */
    void remove(String aggressorUsername, String defendedUsername) throws ShouldNotBeEqualException, NotExistsException;
}
