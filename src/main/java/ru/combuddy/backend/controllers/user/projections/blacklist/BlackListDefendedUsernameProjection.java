package ru.combuddy.backend.controllers.user.projections.blacklist;

import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;

public interface BlackListDefendedUsernameProjection {
    UsernameOnlyUserAccountProjection getDefended();
}
