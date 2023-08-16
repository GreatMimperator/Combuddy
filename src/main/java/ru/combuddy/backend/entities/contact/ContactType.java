package ru.combuddy.backend.entities.contact;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.entities.messaging.Message;

import java.util.List;

@Data
@Entity
public class ContactType {
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 35;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH)
    @Column(length = MAX_NAME_LENGTH, nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contactType")
    private List<PostContact> postContacts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contactType")
    private List<UserContact> postUserContacts;
}
