package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PutMapping("/subscribe/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable String posterUsername, Authentication authentication) {
        var subscriberUsername = authentication.getName();
        try {
            subscriptionService.subscribe(subscriberUsername, posterUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subscriber or poster username not found");
        } catch (ShouldNotBeEqualException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Subscriber and poster usernames should not be equal");
        }
    }

    @DeleteMapping("/unsubscribe/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable String posterUsername, Authentication authentication) {
        var subscriberUsername = authentication.getName();
        try {
            subscriptionService.unsubscribe(subscriberUsername, posterUsername);
        } catch (NotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subscriber or poster username not found");
        } catch (ShouldNotBeEqualException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Subscriber and poster usernames should not be equal");
        }
    }

    @GetMapping("/subscriptions/usernames")
    public UsernamesList getPosterUsernames(Authentication authentication) {
        var subscriberUsername = authentication.getName();
        var subscriptions = subscriptionService.getPosterUsernames(subscriberUsername);
        return new UsernamesList(subscriptions);
    }

    @GetMapping("/subscriptions/beginWith/{posterUsernameBeginPart}/usernames")
    public UsernamesList findPosterUsernamesBeginWith(@PathVariable String posterUsernameBeginPart, Authentication authentication) {
        var subscriberUsername = authentication.getName();
        var posterUsernames = subscriptionService
                .findPosterUsernamesStartedWith(
                        posterUsernameBeginPart,
                        subscriberUsername);
        return new UsernamesList(posterUsernames);
    }


    @GetMapping("/subscribers/usernames")
    public UsernamesList getSubscriberUsernames(Authentication authentication) {
        var posterUsername = authentication.getName();
        var subscribers = subscriptionService.getSubscriberUsernames(posterUsername);
        return new UsernamesList(subscribers);
    }

    @GetMapping("/subscribers/beginWith/{subscriberUsernameBeginPart}/usernames")
    public UsernamesList findSubscriberUsernamesBeginWith(@PathVariable String subscriberUsernameBeginPart, Authentication authentication) {
        var posterUsername = authentication.getName();
        var subscribersUsernames = subscriptionService
                .findSubscriberUsernamesStartedWith(
                        subscriberUsernameBeginPart,
                        posterUsername);
        return new UsernamesList(subscribersUsernames);
    }
}
