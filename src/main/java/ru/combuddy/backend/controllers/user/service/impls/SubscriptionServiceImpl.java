package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;
import ru.combuddy.backend.repositories.user.SubscriptionRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernames;
import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernamesWithAskerExistenceCheck;

@Service
@Transactional
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private SubscriptionRepository subscriptionRepository;
    private UserAccountService userAccountService;

    @Override
    public void subscribe(String subscriberUsername, String posterUsername) throws NotExistsException, ShouldNotBeEqualException {
        usernamesEqualCheck(subscriberUsername, posterUsername);
        var foundSubscriber = userAccountService.findByUsername(subscriberUsername);
        var foundPoster = userAccountService.findByUsername(posterUsername);
        foundCheck(foundSubscriber, subscriberUsername, foundPoster, posterUsername);
        var subscription = new Subscription(null,
                foundSubscriber.get(),
                foundPoster.get());
        var subscriberId = subscription.getSubscriber().getId();
        var posterId = subscription.getPoster().getId();
        if (subscriptionRepository.existsBySubscriberIdAndPosterId(subscriberId, posterId)) {
            return;
        }
        subscriptionRepository.save(subscription);
    }

    @Override
    public void unsubscribe(String subscriberUsername, String posterUsername) throws NotExistsException, ShouldNotBeEqualException {
        usernamesEqualCheck(subscriberUsername, posterUsername);
        var foundSubscriber = userAccountService.findByUsername(subscriberUsername);
        var foundPoster = userAccountService.findByUsername(posterUsername);
        foundCheck(foundSubscriber, subscriberUsername, foundPoster, posterUsername);
        var subscriberId = foundSubscriber.get().getId();
        var posterId = foundPoster.get().getId();
        subscriptionRepository.deleteBySubscriberIdAndPosterId(subscriberId, posterId);
    }

    /**
     * @throws ShouldNotBeEqualException if subscriber and poster username are equal
     */
    private void usernamesEqualCheck(String subscriberUsername, String posterUsername)
            throws ShouldNotBeEqualException {
        if (subscriberUsername.equals(posterUsername)) {
            throw new ShouldNotBeEqualException(
                    MessageFormat.format("Subscriber and poster usernames are equal ({0})",
                            subscriberUsername));
        }
    }

    /**
     * @throws NotExistsException if subscriber or poster are empty
     */
    private void foundCheck(Optional<UserAccount> foundSubscriber, String subscriberQueryUsername,
                                Optional<UserAccount> foundPoster, String posterQueryUsername)
            throws NotExistsException {
        if (foundSubscriber.isEmpty()) {
            throw new NotExistsException(
                    MessageFormat.format("Subscriber {0} doesn't exist",
                            subscriberQueryUsername));
        }
        if (foundPoster.isEmpty()) {
            throw new NotExistsException(
                    MessageFormat.format("Poster {0} doesn't exist",
                            posterQueryUsername));
        }
    }

    @Override
    public List<String> getPosterUsernames(String subscriberUsername) {
        var usernamesList = subscriptionRepository
                .getPosterUsernamesBySubscriberUsername(subscriberUsername);
        return getUsernames(r -> r.getPoster().getUsername(),
                usernamesList);
    }

    @Override
    public List<String> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername) {
        var foundUsernamesList = subscriptionRepository
                .findPosterUsernamesBySubscriberUsernameAndPosterUsernameStartingWith(
                        subscriberUsername,
                        posterUsernameBeginWith);
        return getUsernames(r -> r.getPoster().getUsername(),
                foundUsernamesList);
    }

    @Override
    public List<String> getSubscriberUsernames(String posterUsername) {
        var usernamesList = subscriptionRepository.getSubscriberUsernamesByPosterUsername(posterUsername);
        return getUsernames(r -> r.getSubscriber().getUsername(),
                usernamesList);
    }

    @Override
    public List<String> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername) {
        var usernamesList = subscriptionRepository
                .findSubscriberUsernamesByPosterUsernameAndSubscriberUsernameStartingWith(
                        posterUsername,
                        subscriberUsernameBeginWith);
        return getUsernames(r -> r.getSubscriber().getUsername(),
                usernamesList);
    }

}
