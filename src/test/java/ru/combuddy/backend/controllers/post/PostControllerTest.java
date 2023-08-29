package ru.combuddy.backend.controllers.post;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.entities.contact.BaseContact;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.queries.post.PostControllerQueries;
import ru.combuddy.backend.queries.post.TagControllerQueries;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;
import static ru.combuddy.backend.entities.contact.BaseContact.ContactType.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostControllerQueries postControllerQueries;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserContactService userContactService;

    @Test
    public void creatingRemovingTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var userLoginResponse = loginPreconfigured(mockMvc, username);
        var expectedPostInfo = commonPost(username);
        var postIdAsString = postControllerQueries.create(expectedPostInfo, userLoginResponse.getAccessToken())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var postId = Long.parseLong(postIdAsString);
        var postInfo = postService.getPostInfo(postId, username);
        assertPostsEqual(postInfo, expectedPostInfo);
        assert postInfo.getCreationDate().isPresent();
        assert postInfo.getPostedDate().isPresent();
        assert postInfo.getModificationDate().isEmpty();
        postControllerQueries.remove(postId, userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        try {
            postService.getPostInfo(postId, username);
        } catch (ResponseStatusException e) {
            assertEquals(e.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    public void allUpdatesTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var userLoginResponse = loginPreconfigured(mockMvc, username);
        var expectedPostInfo = commonPost(username);
        var postIdAsString = postControllerQueries.create(expectedPostInfo, userLoginResponse.getAccessToken())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var postId = Long.parseLong(postIdAsString);
        tagService.add("movie", "Smth which harry invented");
        tagService.add("art", "Harry creation of cause");
        expectedPostInfo.setTitle("Oh, i'm sorry");
        expectedPostInfo.setBody("Here we go again, walking like barbie Ken");
        expectedPostInfo.setState(Post.State.HIDDEN);
        expectedPostInfo.setTagNames(List.of("movie", "art"));
        expectedPostInfo.setPostContacts(Optional.of(List.of(new BaseContactInfo(VK, "secret_club"))));
        var newUserContact = new BaseContactInfo(VK, "old_daddy");
        userContactService.add(username, newUserContact.getContactType(), newUserContact.getValue());
        expectedPostInfo.setPostUserContacts(Optional.of(List.of(newUserContact)));
        postControllerQueries.updateTitle(postId,
                        expectedPostInfo.getTitle(),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        postControllerQueries.updateBody(postId,
                        expectedPostInfo.getBody(),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        postControllerQueries.updateState(postId,
                        expectedPostInfo.getState().name(),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        postControllerQueries.updateTags(postId,
                        expectedPostInfo.getTagNames(),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        postControllerQueries.updatePostContacts(postId,
                        new ContactList(expectedPostInfo.getPostContacts().get()),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        postControllerQueries.updateUserContacts(postId,
                        new ContactList(expectedPostInfo.getPostUserContacts().get()),
                        userLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        var postInfo = postService.getPostInfo(postId, username);
        assertPostsEqual(postInfo, expectedPostInfo);
        assert postInfo.getPostedDate().isPresent();
        assert postInfo.getModificationDate().isPresent();
    }

    private PostCreationData commonPost(String creatorUsername) {
        tagService.add("music", "Smth which lil peep invented");
        tagService.add("lil-peep", "Simply the best (imho)");
        var vkUserContact = new BaseContactInfo(VK, "random_user");
        var telegramUserContact = new BaseContactInfo(TELEGRAM, "great_random_user");
        var mailUserContact = new BaseContactInfo(MAIL, "random_user@gmail.com");
        var userContacts = List.of(vkUserContact, telegramUserContact, mailUserContact);
        userContacts.forEach(userContact ->
                userContactService.add(creatorUsername,
                        userContact.getContactType(),
                        userContact.getValue()));
        var postContacts = List.of(new BaseContactInfo(VK, "lil-peep-fans"));
        //building
        var builder = PostCreationData.builder();
        builder.title("Lil peep listeners");
        builder.body("I want to listen lil peep music hugging with somebody :(");
        builder.state(Post.State.POSTED);
        builder.tagNames(List.of("music", "lil-peep"));
        builder.postContacts(Optional.of(postContacts));
        builder.postUserContacts(Optional.of(userContacts));
        return builder.build();
    }

    public void assertPostsEqual(PostInfo postInfo, PostCreationData expectedPostInfo) {
        var titleEquals = postInfo.getTitle().equals(expectedPostInfo.getTitle());
        var bodyEquals = postInfo.getBody().equals(expectedPostInfo.getBody());
        var stateEquals = postInfo.getState() == expectedPostInfo.getState();
        var tagNamesEqual = postInfo.getTagNames().containsAll(expectedPostInfo.getTagNames()) &&
                expectedPostInfo.getTagNames().containsAll(postInfo.getTagNames());
        var postContactsEqual = (postInfo.getPostContacts().isEmpty() && expectedPostInfo.getPostContacts().isEmpty()) ||
                (postInfo.getPostContacts().containsAll(expectedPostInfo.getPostContacts().get()) &&
                        expectedPostInfo.getPostContacts().get().containsAll(postInfo.getPostContacts()));
        var postUserContactsEqual = (postInfo.getPostUserContacts().isEmpty() && expectedPostInfo.getPostUserContacts().isEmpty()) ||
                (postInfo.getPostUserContacts().containsAll(expectedPostInfo.getPostUserContacts().get()) &&
                        expectedPostInfo.getPostUserContacts().get().containsAll(postInfo.getPostUserContacts()));
        assert tagNamesEqual;
        assert titleEquals;
        assert bodyEquals;
        assert stateEquals;
        assert postContactsEqual;
        assert postUserContactsEqual;
    }
}
