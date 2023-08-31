package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.exceptions.permission.user.SelfActionException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    void subscribe(String subscriberUsername, String posterUsername)
            throws UserNotExistsException,
            SelfActionException;

    boolean unsubscribe(String subscriberUsername, String posterUsername); // todo: maybe boolean -> void everywhere

    Optional<Subscription> findSubscription(String posterUsername, String subscriberUsername);

    List<String> getPosterUsernames(String subscriberUsername);

    List<String> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername);

    List<String> getSubscriberUsernames(String posterUsername);

    List<String> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername);

    boolean exists(Long subscriberId, Long posterId);
}
