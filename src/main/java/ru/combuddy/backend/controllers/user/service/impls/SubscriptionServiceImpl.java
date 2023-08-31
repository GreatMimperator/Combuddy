package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.exceptions.permission.user.SelfActionException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.user.SubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserAccountService userAccountService;

    @Override
    public void subscribe(String subscriberUsername, String posterUsername)
            throws UserNotExistsException,
            SelfActionException {
        var subscriber = userAccountService.getByUsername(subscriberUsername);
        var poster = userAccountService.getByUsername(posterUsername);
        if (subscriber.equals(poster)) {
            throw new SelfActionException("Can not subscribe to yourself");
        }
        if (!this.exists(subscriber.getId(), poster.getId())) {
            subscriptionRepository.save(new Subscription(null, subscriber, poster));
        }
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

    @Override
    public boolean exists(Long subscriberId, Long posterId) {
        return subscriptionRepository.existsBySubscriberIdAndPosterId(subscriberId, posterId);
    }
}
