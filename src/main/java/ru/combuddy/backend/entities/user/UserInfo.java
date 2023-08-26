package ru.combuddy.backend.entities.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;

@Entity
@Table(indexes = @Index(columnList = "user_id"))
@JsonPropertyOrder({"id", "userAccount", "registeredDate", "moderator", "mainModerator"}) // todo: как я могу это в json, вдруг там данные приватные?
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserInfo {
    public static final int MAX_PICTURE_THUMBNAIL_KB_SIZE = 128;
    public static final int MAX_FULL_PICTURE_KB_SIZE = 1024;

    public static final int PICTURE_THUMBNAIL_SIZE_PX = 64;
    public static final int PICTURE_FULL_PICTURE_SIZE_PX = 512;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", /*unique = true,*/ nullable = false)
    private UserAccount userAccount;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar registeredDate = Calendar.getInstance();

    @Size(max = MAX_PICTURE_THUMBNAIL_KB_SIZE * 1024)
    @Lob
    private byte[] pictureThumbnail;

    @Size(max = MAX_FULL_PICTURE_KB_SIZE * 1024)
    @Lob
    private byte[] fullPicture;


    public UserInfo(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public static boolean isFullPictureSize(int width, int height) {
        return width == PICTURE_FULL_PICTURE_SIZE_PX &&
                height == PICTURE_FULL_PICTURE_SIZE_PX;
    }

    public static boolean isPictureThumbnailSize(int width, int height) {
        return width == PICTURE_THUMBNAIL_SIZE_PX &&
                height == PICTURE_THUMBNAIL_SIZE_PX;
    }
}
