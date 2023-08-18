package ru.combuddy.backend.repositories.user;

import org.springframework.data.repository.CrudRepository;
import ru.combuddy.backend.controllers.user.projections.info.FullPictureProjection;
import ru.combuddy.backend.controllers.user.projections.info.ThumbnailProjection;
import ru.combuddy.backend.entities.user.UserInfo;

import java.util.Optional;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    Optional<ThumbnailProjection> findThumbnailByUserAccountUsername(String username);

    Optional<FullPictureProjection> findFullPictureByUserAccountUsername(String username);
}
