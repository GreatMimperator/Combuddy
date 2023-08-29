package ru.combuddy.backend.converters;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommaSepListConverter implements TagListConverter {
    @Override
    public List<String> convert(String commaSepTagList) {
        return Arrays.stream(commaSepTagList.split(","))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
