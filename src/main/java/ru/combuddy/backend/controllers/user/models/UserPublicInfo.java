package ru.combuddy.backend.controllers.user.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.Calendar;
import java.util.List;

@JsonPropertyOrder({"username", "frozen", "registeredDate", "roles", "subscriptions"})
@Data
@Builder
public class UserPublicInfo {
    private final String username;
    private final boolean frozen;
    private final Calendar registeredDate;
    private final List<String> roles;
    private final List<String> subscriptions;
}
