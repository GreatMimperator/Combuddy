package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.subscription.SubscriptionPosterUsernameProjection;
import ru.combuddy.backend.controllers.user.projections.subscription.SubscriptionSubscriberUsernameProjection;
import ru.combuddy.backend.entities.user.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    boolean existsBySubscriberIdAndPosterId(long subscriberId, long posterId);

    int deleteBySubscriberUsernameAndPosterUsername(String subscriberUsername, String posterUsername);

    List<SubscriptionPosterUsernameProjection> getPosterUsernamesBySubscriberUsername(String subscriberUsername);

    List<SubscriptionPosterUsernameProjection> findPosterUsernamesBySubscriberUsernameAndPosterUsernameStartingWith(
            String subscriberUsername,
            String posterUsernameStartedWith);

    List<SubscriptionSubscriberUsernameProjection> getSubscriberUsernamesByPosterUsername(String posterUsername);

    List<SubscriptionSubscriberUsernameProjection> findSubscriberUsernamesByPosterUsernameAndSubscriberUsernameStartingWith(
            String posterUsername,
            String subscriberUsernameStartedWith);

    Optional<Subscription> findSubscriptionByPosterUsernameAndSubscriberUsername(
            String posterUsername,
            String subscriberUsername);
}
