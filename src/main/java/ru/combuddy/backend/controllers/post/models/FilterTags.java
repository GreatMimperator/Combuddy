package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"includeTagNames", "excludeTagNames"})
public class FilterTags {
    @JsonAlias("includeTags")
    private List<String> includeTagNames;
    @JsonAlias("excludeTags")
    private List<String> excludeTagNames;

    public FilterTags(List<String> includeTagNames, List<String> excludeTagNames) {
        this.includeTagNames = includeTagNames;
        this.excludeTagNames = excludeTagNames;
    }
}
