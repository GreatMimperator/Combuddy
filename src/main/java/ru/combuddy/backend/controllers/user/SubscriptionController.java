package ru.combuddy.backend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.user.models.UsernamesList;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PutMapping("/subscribe/{subscriberUsername}/to/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable String subscriberUsername, @PathVariable String posterUsername) {
        var subscribed = subscriptionService.subscribe(subscriberUsername, posterUsername);
        if (!subscribed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subscriber or poster username not found");
        }
    }

    @DeleteMapping("/unsubscribe/{subscriberUsername}/to/{posterUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable String subscriberUsername, @PathVariable String posterUsername) {
        var unsubscribed = subscriptionService.unsubscribe(subscriberUsername, posterUsername);
        if (!unsubscribed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subscriber or poster username not found");
        }
    }

    @GetMapping("/subscriptions/{subscriberUsername}")
    public UsernamesList getPosterUsernames(@PathVariable String subscriberUsername) {
        var foundSubscriptions = subscriptionService.getPosterUsernames(subscriberUsername);
        if (foundSubscriptions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return new UsernamesList(foundSubscriptions.get());
    }

    @GetMapping("/subscriptions/beginWith/{posterUsernameBeginPart}/of/{subscriberUsername}")
    public UsernamesList findPosterUsernamesBeginWith(@PathVariable String posterUsernameBeginPart,
                                                     @PathVariable String subscriberUsername) {
        var foundPosterUsernames = subscriptionService
                .findPosterUsernamesStartedWith(
                        posterUsernameBeginPart,
                        subscriberUsername);
        if (foundPosterUsernames.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this subscriber username do not exist");
        }
        return new UsernamesList(foundPosterUsernames.get());
    }


    @GetMapping("/subscribers/{posterUsername}")
    public UsernamesList getSubscriberUsernames(@PathVariable String posterUsername) {
        var foundSubscribers = subscriptionService.getSubscriberUsernames(posterUsername);
        if (foundSubscribers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this username do not exist");
        }
        return new UsernamesList(foundSubscribers.get());
    }

    @GetMapping("/subscribers/beginWith/{subscriberUsernameBeginPart}/of/{posterUsername}")
    public UsernamesList findSubscriberUsernamesBeginWith(@PathVariable String subscriberUsernameBeginPart,
                                                          @PathVariable String posterUsername) {
        var foundSubscribersUsernames = subscriptionService
                .findSubscriberUsernamesStartedWith(
                        subscriberUsernameBeginPart,
                        posterUsername);
        if (foundSubscribersUsernames.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Users with this poster username do not exist");
        }
        return new UsernamesList(foundSubscribersUsernames.get());
    }
}
