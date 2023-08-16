package ru.combuddy.backend.converters;

import org.springframework.core.convert.converter.Converter;

import java.util.List;

public interface TagListConverter extends Converter<String, List<String>> {
}
