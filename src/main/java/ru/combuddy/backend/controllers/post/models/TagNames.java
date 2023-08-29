package ru.combuddy.backend.controllers.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.post.tag.Tag;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagNames {
    List<String> tagNames;
}
