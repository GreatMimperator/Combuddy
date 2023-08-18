package ru.combuddy.backend.entities.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.post.PostComplaint;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.messaging.Dialog;
import ru.combuddy.backend.entities.messaging.PublicMessage;
import ru.combuddy.backend.entities.tag.PostTag;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.Calendar;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Post {
    public static final int MIN_TITLE_LENGTH = 20;
    public static final int MAX_TITLE_LENGTH = 150;

    public static final int MIN_BODY_LENGTH = 20;
    public static final int MAX_BODY_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @NotNull
    @Size(min = MIN_TITLE_LENGTH, max = MAX_TITLE_LENGTH)
    @Column(length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @NotNull
    @Size(min = MIN_BODY_LENGTH, max = MAX_BODY_LENGTH)
    @Column(length = MAX_BODY_LENGTH, nullable = false)
    private String body;

    @NotNull
    @Column(nullable = false)
    private Boolean frozen = false;

    @NotNull
    @Column(nullable = false)
    private Boolean archived = false;

    @NotNull
    @Column(nullable = false)
    private Boolean hidden = false;

    @NotNull
    @Column(nullable = false)
    private Boolean draft = false;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar creationDate = Calendar.getInstance();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar modificationDate = Calendar.getInstance();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<PostContact> postContacts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<PostUserContact> postUserContacts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<PostTag> tags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topicPost")
    private List<Dialog> relatedDialogs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<PublicMessage> publicMessages;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<FavoritePost> favoriteList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "suspectPost")
    private List<PostComplaint> complaintsAsSuspect;
}
