package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.repositories.user.SubscriptionRepository;

import java.util.List;
import java.util.Optional;

import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernamesWithAskerExistenceCheck;

@Service
@Transactional
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private SubscriptionRepository subscriptionRepository;
    private UserAccountService userAccountService;

    @Override
    public boolean subscribe(String subscriberUsername, String posterUsername) {
        var foundSubscriber = userAccountService.findByUsername(subscriberUsername);
        var foundPoster = userAccountService.findByUsername(posterUsername);
        if (foundSubscriber.isEmpty() || foundPoster.isEmpty()) {
            return false;
        }
        var subscription = new Subscription(null,
                foundSubscriber.get(),
                foundPoster.get());
        var subscriberId = subscription.getSubscriber().getId();
        var posterId = subscription.getPoster().getId();
        if (subscriptionRepository.existsBySubscriberIdAndPosterId(subscriberId, posterId)) {
            return true;
        }
        subscriptionRepository.save(subscription);
        return true;
    }

    @Override
    public boolean unsubscribe(String subscriberUsername, String posterUsername) {
        var foundSubscriber = userAccountService.findByUsername(subscriberUsername);
        var foundPoster = userAccountService.findByUsername(posterUsername);
        if (foundSubscriber.isEmpty() || foundPoster.isEmpty()) {
            return false;
        }
        var subscriberId = foundSubscriber.get().getId();
        var posterId = foundPoster.get().getId();
        subscriptionRepository.deleteBySubscriberIdAndPosterId(subscriberId, posterId);
        return true;
    }

    @Override
    public Optional<List<String>> getPosterUsernames(String subscriberUsername) {
        var usernamesList = subscriptionRepository
                .getPosterUsernamesBySubscriberUsername(subscriberUsername);
        return getUsernamesWithAskerExistenceCheck(r -> r.getPoster().getUsername(),
                usernamesList,
                subscriberUsername,
                userAccountService);
    }

    @Override
    public Optional<List<String>> findPosterUsernamesStartedWith(String posterUsernameBeginWith, String subscriberUsername) {
        var foundUsernamesList = subscriptionRepository
                .findPosterUsernamesBySubscriberUsernameAndPosterUsernameStartingWith(
                        subscriberUsername,
                        posterUsernameBeginWith);
        return getUsernamesWithAskerExistenceCheck(r -> r.getPoster().getUsername(),
                foundUsernamesList,
                subscriberUsername,
                userAccountService);
    }

    @Override
    public Optional<List<String>> getSubscriberUsernames(String posterUsername) {
        var usernamesList = subscriptionRepository.getSubscriberUsernamesByPosterUsername(posterUsername);
        return getUsernamesWithAskerExistenceCheck(r -> r.getSubscriber().getUsername(),
                usernamesList,
                posterUsername,
                userAccountService);
    }

    @Override
    public Optional<List<String>> findSubscriberUsernamesStartedWith(String subscriberUsernameBeginWith, String posterUsername) {
        var usernamesList = subscriptionRepository
                .findSubscriberUsernamesByPosterUsernameAndSubscriberUsernameStartingWith(
                        posterUsername,
                        subscriberUsernameBeginWith);
        return getUsernamesWithAskerExistenceCheck(r -> r.getSubscriber().getUsername(),
                usernamesList,
                posterUsername,
                userAccountService);
    }

}
