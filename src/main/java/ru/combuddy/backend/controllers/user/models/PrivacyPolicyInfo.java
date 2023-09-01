package ru.combuddy.backend.controllers.user.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.PrivacyPolicy;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivacyPolicyInfo {
    @NotNull
    PrivacyPolicy.RegisteredDateAccessLevel registeredDateAccessLevel;
    @NotNull
    PrivacyPolicy.SubscriptionsAccessLevel subscriptionsAccessLevel;

    public PrivacyPolicyInfo(PrivacyPolicy privacyPolicy) {
        setRegisteredDateAccessLevel(privacyPolicy.getRegisteredDateAccessLevel());
        setSubscriptionsAccessLevel(privacyPolicy.getSubscriptionsAccessLevel());
    }
}
