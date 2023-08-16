package ru.combuddy.backend.controllers.user.projections.info;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "frozen"})
public interface PublicInfoUserAccountProjection {
    String getUsername();
    boolean isFrozen();
}
