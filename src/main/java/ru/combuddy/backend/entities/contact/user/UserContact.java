package ru.combuddy.backend.entities.contact.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "contactType", "value"}))
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserContact extends BaseContact {
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    public UserContact(Long id, UserAccount owner, ContactType contactType, String value) {
        super(id, contactType, value);
        this.owner = owner;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userContact")
    private List<PostUserContact> postUserContacts;
}
