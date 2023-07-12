package ru.combuddy.backend.controllers.user.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsernamesList {
    List<String> usernames;
}
