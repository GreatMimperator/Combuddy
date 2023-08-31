package ru.combuddy.backend.entities.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.post.PostComplaint;
import ru.combuddy.backend.entities.contact.post.PostContact;
import ru.combuddy.backend.entities.contact.post.PostUserContact;
import ru.combuddy.backend.entities.messaging.Dialog;
import ru.combuddy.backend.entities.messaging.PublicMessage;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.post.IllegalPostStateException;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Post {
    public static final int MIN_TITLE_LENGTH = 10;
    public static final int MAX_TITLE_LENGTH = 150;

    public static final int MIN_BODY_LENGTH = 20;
    public static final int MAX_BODY_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @Size(min = MIN_TITLE_LENGTH, max = MAX_TITLE_LENGTH)
    @Column(length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Size(min = MIN_BODY_LENGTH, max = MAX_BODY_LENGTH)
    @Column(length = MAX_BODY_LENGTH, nullable = false)
    private String body;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        POSTED, DRAFT, HIDDEN, FROZEN;

        /**
         * Converts using {@link #name()} call and ignoring case in equal check
         */
        public static State convertToState(String stateAsString) throws IllegalPostStateException {
            for (var state : State.values()) {
                if (state.name().equalsIgnoreCase(stateAsString)) {
                    return state;
                }
            }
            throw new IllegalPostStateException(
                    MessageFormat.format("Role name {0} does not exist as enum value",
                            stateAsString));
        }
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar creationDate = Calendar.getInstance();

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Calendar modificationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Calendar postedDate;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY,orphanRemoval = true, mappedBy = "post")
    private List<PostContact> postContacts;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "post")
    private List<PostUserContact> postUserContacts;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY,orphanRemoval = true, mappedBy = "post")
    private List<PostTag> tags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topicPost")
    private List<Dialog> relatedDialogs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<PublicMessage> publicMessages;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<FavoritePost> favoritedList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "suspectPost")
    private List<PostComplaint> complaintsAsSuspect;
}
