package ru.combuddy.backend.controllers.user.service.interfaces;

import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    List<String> getPosterUsernames(String subscriberUsername);

    List<String> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername);

    List<String> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername);

    List<String> getSubscriberUsernames(String posterUsername);

    /**
     * @throws NotExistsException if any of user do not exist
     * @throws ShouldNotBeEqualException if subscriber and poster username are equal
     */
    void subscribe(String subscriberUsername, String posterUsername) throws NotExistsException, ShouldNotBeEqualException;

    /**
     * @throws NotExistsException if any of user do not exist
     * @throws ShouldNotBeEqualException if subscriber and poster username are equal
     */
    void unsubscribe(String subscriberUsername, String posterUsername) throws NotExistsException, ShouldNotBeEqualException;

}
