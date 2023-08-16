package ru.combuddy.backend.controllers.user.projections.subscription;

import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;

public interface SubscriptionSubscriberUsernameProjection {
    UsernameOnlyUserAccountProjection getSubscriber();
}
