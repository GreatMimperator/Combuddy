package ru.combuddy.backend.controllers.post.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.Calendar;
import java.util.List;

@Data
@JsonPropertyOrder({"id"})
public class OwnPostReceiveData {
    private long id;
    private String title;
    private String body;
    private boolean frozen;
    private boolean archived;
    private boolean hidden;
    private boolean draft;
    private Calendar creationDate;
    private Calendar modificationDate;
    private List<String> tags;
}
