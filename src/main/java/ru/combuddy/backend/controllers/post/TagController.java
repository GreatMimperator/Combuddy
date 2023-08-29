package ru.combuddy.backend.controllers.post;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.controllers.post.models.TagNames;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.converters.CommaSepListConverter;

@RestController
@RequestMapping("/api/post/tag")
@Transactional
@AllArgsConstructor
public class TagController {

    private final TagService tagService;
    private final CommaSepListConverter commaSepListConverter;

    @PutMapping("/add/{name}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable String name,
                    @RequestParam String description) {
        if (tagService.exists(name)) {
            return;
        }
        tagService.add(name, description);
    }

    @DeleteMapping("/remove/{name}")
    @PreAuthorize("hasRole('MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String name) {
        tagService.delete(name);
    }

    @PatchMapping("/change-description/{name}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'MAIN_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeDescription(@PathVariable String name,
                                  @RequestParam String description) {
        var foundTag = tagService.find(name);
        if (foundTag.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tag with this name does not exist");
        }
        var tag = foundTag.get();
        tag.setDescription(description);
        tagService.save(tag);
    }

    @GetMapping("/names/beginWith/{nameBeginPart}")
    public TagNames getBeginWith(@PathVariable String nameBeginPart) {
        var tagNames = tagService.findNamesByNameStartingWith(nameBeginPart);
        return new TagNames(tagNames);
    }

    @GetMapping("/names/all")
    public TagNames getAllNames() {
        var tagNames = tagService.getAllNames();
        return new TagNames(tagNames);
    }

    @GetMapping("/description/{name}")
    public String getDescription(@PathVariable String name) {
        var foundTag = tagService.find(name);
        if (foundTag.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tag with this name does not exist");
        }
        return foundTag.get().getDescription();
    }

    @PutMapping("/home/set")
    public void homeTagsSet(@RequestParam String commaSeparatedIncludedTags,
                            @RequestParam String commaSeparatedExcludedTags,
                            Authentication authentication) {
        var updaterUsername = authentication.getName();
        var includedTags = commaSepListConverter.convert(commaSeparatedIncludedTags);
        var excludedTags = commaSepListConverter.convert(commaSeparatedExcludedTags);
        tagService.homeTagsUpdate(includedTags, excludedTags, updaterUsername);
    }

    @GetMapping("/home/get")
    public FilterTags getHomeTags(Authentication authentication) {
        var receiverUsername = authentication.getName();
        return tagService.getHomeTags(receiverUsername);
    }

}
