package ru.combuddy.backend.entities.contact.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "contactType", "value"}))
public class PostContact extends BaseContact {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostContact(Long id, Post post, ContactType contactType, String contact) {
        super(id, contactType, contact);
        this.post = post;
    }


    public static List<BaseContactInfo> toBaseContactInfo(List<PostContact> postContacts) {
        return postContacts.stream()
                .map(BaseContactInfo::new)
                .toList();
    }
}
