package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.controllers.user.models.UsernamesList;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    Optional<List<String>> getPosterUsernames(String subscriberUsername);

    /**
     * @return empty if subscriber does not exist
     */
    Optional<List<String>> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername);

    /**
     * @return empty if poster does not exist
     */
    Optional<List<String>> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername);

    Optional<List<String>> getSubscriberUsernames(String posterUsername);

    /**
     * @return false if any of user do not exist, true if subscribed now
     */
    boolean subscribe(String subscriberUsername, String posterUsername);

    /**
     * @return false if any of user do not exist, true if not subscribed now
     */
    boolean unsubscribe(String subscriberUsername, String posterUsername);

}
