package ru.combuddy.backend.entities.contact.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_contact_id"}))
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class PostUserContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_contact_id", nullable = false)
    private UserContact userContact;


    public static List<BaseContactInfo> toBaseContactInfo(List<PostUserContact> postContacts) {
        return postContacts.stream()
                .map(PostUserContact::getUserContact)
                .map(BaseContactInfo::new)
                .toList();
    }
}
