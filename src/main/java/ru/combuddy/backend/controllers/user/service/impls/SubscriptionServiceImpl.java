package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;
import ru.combuddy.backend.repositories.user.SubscriptionRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private SubscriptionRepository subscriptionRepository;
    private UserAccountService userAccountService;

    @Transactional
    @Override
    public void subscribe(String subscriberUsername, String posterUsername) throws ShouldNotBeEqualException, NotExistsException {
        usernamesEqualCheck(subscriberUsername, posterUsername);
        var subscriber = userAccountService.getByUsername(subscriberUsername, "subscriber");
        var poster = userAccountService.getByUsername(posterUsername, "poster");
        if (subscriptionRepository.existsBySubscriberIdAndPosterId(subscriber.getId(), poster.getId())) {
            return;
        }
        subscriptionRepository.save(new Subscription(null, subscriber, poster));
    }

    @Override
    public boolean unsubscribe(String subscriberUsername, String posterUsername) {
        var deletedCount = subscriptionRepository.deleteBySubscriberUsernameAndPosterUsername(
                subscriberUsername,
                posterUsername);
        return deletedCount > 0;
    }

    @Override
    public Optional<Subscription> findSubscription(String posterUsername, String subscriberUsername) {
        return subscriptionRepository.findSubscriptionByPosterUsernameAndSubscriberUsername(
                posterUsername,
                subscriberUsername);
    }

    @Override
    public List<String> getPosterUsernames(String subscriberUsername) {
        var usernamesList = subscriptionRepository
                .getPosterUsernamesBySubscriberUsername(subscriberUsername);
        return usernamesList.stream()
                .map(r -> r.getPoster().getUsername())
                .toList();
    }

    @Override
    public List<String> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername) {
        var foundUsernamesList = subscriptionRepository
                .findPosterUsernamesBySubscriberUsernameAndPosterUsernameStartingWith(
                        subscriberUsername,
                        posterUsernameBeginWith);
        return foundUsernamesList.stream()
                .map(r -> r.getPoster().getUsername())
                .toList();
    }

    @Override
    public List<String> getSubscriberUsernames(String posterUsername) {
        var usernamesList = subscriptionRepository.getSubscriberUsernamesByPosterUsername(posterUsername);
        return usernamesList.stream()
                .map(r -> r.getSubscriber().getUsername())
                .toList();
    }

    @Override
    public List<String> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername) {
        var usernamesList = subscriptionRepository
                .findSubscriberUsernamesByPosterUsernameAndSubscriberUsernameStartingWith(
                        posterUsername,
                        subscriberUsernameBeginWith);
        return usernamesList.stream()
                .map(r -> r.getSubscriber().getUsername())
                .toList();
    }

    /**
     * @throws ShouldNotBeEqualException if subscriber and poster usernames are equal
     */
    private void usernamesEqualCheck(String subscriberUsername, String posterUsername)
            throws ShouldNotBeEqualException {
        if (subscriberUsername.equals(posterUsername)) {
            throw new ShouldNotBeEqualException(
                    MessageFormat.format("subscriber and poster usernames are equal ({0})",
                            subscriberUsername));
        }
    }
}
