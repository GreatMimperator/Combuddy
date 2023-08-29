package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.post.tag.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"name", "description"})
public class TagInfo {
    private String name;
    private String description;

    public TagInfo(Tag tag) {
        this.name = tag.getName();
        this.description = tag.getDescription();
    }
}
