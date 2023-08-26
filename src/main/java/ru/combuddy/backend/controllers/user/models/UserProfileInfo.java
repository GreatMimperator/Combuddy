package ru.combuddy.backend.controllers.user.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@JsonPropertyOrder({"username", "frozen", "roles", "permittedToSee", "registeredDate", "subscriptions"})
@Data
@Builder
public class UserProfileInfo {
    private final String username;
    private final boolean frozen;
    private final String role;

    private final PermittedToSee permittedToSee;
    // permission-dependent fields
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private final Optional<Calendar> registeredDate;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private final Optional<List<String>> subscriptions;

    @JsonCreator
    public UserProfileInfo(
            @JsonProperty("username") String username,
            @JsonProperty("frozen") boolean frozen,
            @JsonProperty("role") String role,
            @JsonProperty("permittedToSee") PermittedToSee permittedToSee,
            @JsonProperty("registeredDate") Optional<Calendar> registeredDate,
            @JsonProperty("subscriptions") Optional<List<String>> subscriptions) {
        this.username = username;
        this.frozen = frozen;
        this.role = role;
        this.permittedToSee = permittedToSee;
        this.registeredDate = registeredDate;
        this.subscriptions = subscriptions;
    }

    @JsonPropertyOrder({"registeredDate", "subscriptions"})
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermittedToSee {
        boolean registeredDate;
        boolean subscriptions;
    }
}
