package ru.combuddy.backend.controllers.user.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamesList {
    List<String> usernames;
}
