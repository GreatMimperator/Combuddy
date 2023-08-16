package ru.combuddy.backend.entities.contact.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "contact_type_id"}))
public class UserContact extends BaseContact {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userContact")
    private List<PostUserContact> postUserContacts;
}
