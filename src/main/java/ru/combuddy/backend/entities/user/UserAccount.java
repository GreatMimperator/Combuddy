package ru.combuddy.backend.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.combuddy.backend.entities.complain.post.PostComplaintJudgment;
import ru.combuddy.backend.entities.complain.post.PostComplaint;
import ru.combuddy.backend.entities.complain.user.UserComplaint;
import ru.combuddy.backend.entities.complain.user.UserComplaintJudgment;
import ru.combuddy.backend.entities.contact.user.UserContact;
import ru.combuddy.backend.entities.messaging.Dialog;
import ru.combuddy.backend.entities.messaging.PublicMessage;
import ru.combuddy.backend.entities.post.FavoritePost;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.UserHomeTag;
import ru.combuddy.backend.security.entities.UserBaseAuth;
import ru.combuddy.backend.security.entities.Role;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class UserAccount {
    public static final int MIN_USERNAME_LENGTH = 5;
    public static final int MAX_USERNAME_LENGTH = 35;

    public UserAccount(String username) {
        setUsername(username);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = MIN_USERNAME_LENGTH,
            max = MAX_USERNAME_LENGTH) // todo: add this like to anything and catch exception everywhere
    @Column(length = MAX_USERNAME_LENGTH,
            unique = true,
            nullable = false)
    private String username;

    @NotNull
    @Column(nullable = false)
    private Boolean frozen = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userAccount")
    private UserInfo userInfo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userAccount")
    private PrivacyPolicy privacyPolicy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userAccount")
    private UserBaseAuth baseAuth;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "owner")
    private List<UserContact> contacts;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserHomeTag> homeTags;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "subscriber")
    private List<Subscription> subscriptions;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "poster")
    private List<Subscription> subscribers;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "subscriber")
    private List<FavoritePost> favoritePosts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Post> ownedPosts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "suspect")
    private List<UserComplaint> userComplaintsAsSuspect;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "informer")
    private List<UserComplaint> userComplaintsAsInformer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "informer")
    private List<PostComplaint> postComplaintsAsInformer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "judge")
    private List<UserComplaintJudgment> userComplaintJudgmentsAsJudge;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "judge")
    private List<PostComplaintJudgment> postComplaintJudgmentsAsJudge;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "defended")
    private List<BlackList> blackListDefended;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aggressor")
    private List<BlackList> blackListAggressor;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "firstParticipant")
    private List<Dialog> dialogsAsFirstParticipant;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "secondParticipant")
    private List<Dialog> dialogsAsSecondParticipant;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private List<PublicMessage> publicMessages;

}
