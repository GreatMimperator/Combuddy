package ru.combuddy.backend.entities.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Data
@Entity
@Table(indexes = @Index(columnList = "user_id"))
@JsonPropertyOrder({"id", "userAccount", "registeredDate", "moderator", "mainModerator"})
public class UserInfo {
    public static final int MAX_PICTURE_THUMBNAIL_KB_SIZE = 128;
    public static final int MAX_FULL_PICTURE_KB_SIZE = 1024;

    public static final int PICTURE_THUMBNAIL_SIZE_PX = 64;
    public static final int PICTURE_FULL_PICTURE_SIZE_PX = 512;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private UserAccount userAccount;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar registeredDate = Calendar.getInstance();

    @NotNull
    @Column(nullable = false)
    private Boolean moderator = false;

    @NotNull
    @Column(nullable = false)
    private Boolean mainModerator = false;

    @Size(max = MAX_PICTURE_THUMBNAIL_KB_SIZE * 1024)
    @Lob
    private byte[] pictureThumbnail;

    @Size(max = MAX_FULL_PICTURE_KB_SIZE * 1024)
    @Lob
    private byte[] fullPicture;

    public static boolean isFullPictureSize(int width, int height) {
        return width == PICTURE_FULL_PICTURE_SIZE_PX &&
                height == PICTURE_FULL_PICTURE_SIZE_PX;
    }

    public static boolean isPictureThumbnailSize(int width, int height) {
        return width == PICTURE_THUMBNAIL_SIZE_PX &&
                height == PICTURE_THUMBNAIL_SIZE_PX;
    }
}
