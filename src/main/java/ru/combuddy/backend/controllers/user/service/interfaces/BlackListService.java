package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.exceptions.permission.user.SelfActionException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.util.List;
import java.util.Optional;

public interface BlackListService {
    void add(String aggressorUsername, String defendedUsername)
            throws UserNotExistsException,
            SelfActionException;

    boolean delete(String aggressorUsername, String defendedUsername);

    List<String> getAggressorUsernames(String defendedUsername);

    Optional<BlackList> findRecord(String aggressorUsername, String defendedUsername);

    boolean exists(Long aggressorId, Long defendedId);
}
