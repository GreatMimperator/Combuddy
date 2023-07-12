package ru.combuddy.backend.controllers.user.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import lombok.Data;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.entities.user.UserInfo;

@Data
@JsonPropertyOrder({"userAccount", "userInfo"})
public class User {
    @Valid
    UserAccount userAccount;
    @Valid
    UserInfo userInfo;
}
