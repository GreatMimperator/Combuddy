package ru.combuddy.backend.controllers.post;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.controllers.post.models.TagNames;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.converters.CommaSepListConverter;

import static ru.combuddy.backend.controllers.user.AuthController.getUsername;

@RestController
@RequestMapping("/api/v1/post/tag")
@Transactional
@AllArgsConstructor
public class TagController {

    private final TagService tagService;
    private final CommaSepListConverter commaSepListConverter;

    @PutMapping("/add/{name}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable String name,
                    @RequestParam String description) {
        tagService.put(name, description);
    }

    @DeleteMapping("/delete/{name}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        tagService.delete(name);
    }

    @PatchMapping("/change/description/{name}")
    @PreAuthorize("@authorityComparator.overOrEqual(authentication, 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDescription(@PathVariable String name,
                                  @RequestParam String description) {
        tagService.updateDescription(name, description);
    }

    @GetMapping("/names/beginWith/{nameBeginPart}")
    public TagNames getBeginWith(@PathVariable String nameBeginPart) {
        return new TagNames(tagService.findNamesByNameStartingWith(nameBeginPart));
    }

    @GetMapping("/names/all")
    public TagNames getAllNames() {
        return new TagNames(tagService.getAllNames());
    }

    @GetMapping("/description/{name}")
    public String getDescription(@PathVariable String name) {
        return tagService.getDescription(name);
    }

    @PutMapping("/home/set")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void homeTagsSet(@RequestParam String commaSeparatedIncludedTags,
                            @RequestParam String commaSeparatedExcludedTags,
                            Authentication authentication) {
        var updaterUsername = getUsername(authentication);
        var includedTags = commaSepListConverter.convert(commaSeparatedIncludedTags);
        var excludedTags = commaSepListConverter.convert(commaSeparatedExcludedTags);
        tagService.homeTagsUpdate(includedTags, excludedTags, updaterUsername);
    }

    @GetMapping("/home/get")
    public FilterTags getHomeTags(Authentication authentication) {
        var receiverUsername = getUsername(authentication);
        return tagService.getFilterTags(receiverUsername);
    }

}
