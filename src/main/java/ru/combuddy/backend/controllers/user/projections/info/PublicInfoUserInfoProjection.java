package ru.combuddy.backend.controllers.user.projections.info;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Calendar;

@JsonPropertyOrder({"userAccount", "registeredDate", "moderator", "mainModerator"})
public interface PublicInfoUserInfoProjection {
    PublicInfoUserAccountProjection getUserAccount();

    Calendar getRegisteredDate();

    boolean isModerator();

    boolean isMainModerator();
}
