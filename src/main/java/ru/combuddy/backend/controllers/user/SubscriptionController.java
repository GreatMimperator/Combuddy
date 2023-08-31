package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PutMapping("/subscribe/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable String posterUsername, Authentication authentication) {
        var subscriberUsername = getUsername(authentication);
        subscriptionService.subscribe(subscriberUsername, posterUsername);
    }

    @DeleteMapping("/unsubscribe/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable String posterUsername, Authentication authentication) {
        var subscriberUsername = getUsername(authentication);
        subscriptionService.unsubscribe(subscriberUsername, posterUsername);
    }

    @GetMapping("/subscriptions/usernames")
    public UsernamesList getPosterUsernames(Authentication authentication) {
        var subscriberUsername = getUsername(authentication);
        var subscriptions = subscriptionService.getPosterUsernames(subscriberUsername);
        return new UsernamesList(subscriptions);
    }

    @GetMapping("/subscriptions/usernames/beginWith/{posterUsernameBeginPart}")
    public UsernamesList findPosterUsernamesBeginWith(@PathVariable String posterUsernameBeginPart,
                                                      Authentication authentication) {
        var subscriberUsername = getUsername(authentication);
        var posterUsernames = subscriptionService
                .findPosterUsernamesStartedWith(
                        posterUsernameBeginPart,
                        subscriberUsername);
        return new UsernamesList(posterUsernames);
    }


    @GetMapping("/subscribers/usernames")
    public UsernamesList getSubscriberUsernames(Authentication authentication) {
        var posterUsername = getUsername(authentication);
        var subscribers = subscriptionService.getSubscriberUsernames(posterUsername);
        return new UsernamesList(subscribers);
    }

    @GetMapping("/subscribers/usernames/beginWith/{subscriberUsernameBeginPart}")
    public UsernamesList findSubscriberUsernamesBeginWith(@PathVariable String subscriberUsernameBeginPart, Authentication authentication) {
        var posterUsername = getUsername(authentication);
        var subscribersUsernames = subscriptionService
                .findSubscriberUsernamesStartedWith(
                        subscriberUsernameBeginPart,
                        posterUsername);
        return new UsernamesList(subscribersUsernames);
    }
}
