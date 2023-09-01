package ru.combuddy.backend.controllers.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.ServiceConstants;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.contact.models.ContactList;
import ru.combuddy.backend.controllers.contact.service.interfaces.UserContactService;
import ru.combuddy.backend.controllers.post.models.IdsList;
import ru.combuddy.backend.controllers.post.models.PostCreationData;
import ru.combuddy.backend.controllers.post.models.PostInfo;
import ru.combuddy.backend.controllers.post.models.TagInfo;
import ru.combuddy.backend.controllers.post.service.interfaces.FavouritePostService;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.controllers.user.service.interfaces.SubscriptionService;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.exceptions.post.InvalidPostIdException;
import ru.combuddy.backend.queries.post.PostControllerQueries;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.Util.listEqualsIgnoreOrder;
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
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private FavouritePostService favouritePostService;

    @Autowired
    private ServiceConstants serviceConstants;

    @Test
    public void creatingRemovingTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var accessToken = loginPreconfigured(mockMvc, username);
        var expectedPostInfo = commonPost(username);
        var postIdAsString = postControllerQueries.create(expectedPostInfo, accessToken)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var postId = Long.parseLong(postIdAsString);
        var postInfo = postService.getPostInfo(postId, username);
        assertPostsEqual(postInfo, expectedPostInfo);
        assert postInfo.getCreationDate().isPresent();
        assert postInfo.getPostedDate().isPresent();
        assert postInfo.getModificationDate().isEmpty();
        postControllerQueries.delete(postId, accessToken)
                .andExpect(status().isNoContent());
        boolean postNotExist = false;
        try {
            postService.getPostInfo(postId, username);
        } catch (InvalidPostIdException e) {
            postNotExist = true;
        }
        assert postNotExist;
    }

    @Test
    public void allUpdatesTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var accessToken = loginPreconfigured(mockMvc, username);
        var expectedPostInfo = commonPost(username);
        var postIdAsString = postControllerQueries.create(expectedPostInfo, accessToken)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var postId = Long.parseLong(postIdAsString);
        tagService.add("movie", "Smth which harry invented");
        tagService.add("art", "Harry creation of cause");
        expectedPostInfo.setTitle("Oh, i'm sorry");
        expectedPostInfo.setBody("Here we go again, walking like barbie Ken");
        expectedPostInfo.setState(Post.State.HIDDEN);
        expectedPostInfo.setTags(List.of("movie", "art"));
        expectedPostInfo.setPostContacts(List.of(new BaseContactInfo(VK, "secret_club")));
        var newUserContact = new BaseContactInfo(VK, "old_daddy");
        userContactService.add(username, newUserContact.getContactType(), newUserContact.getValue());
        expectedPostInfo.setUserContacts(List.of(newUserContact));
        postControllerQueries.updateTitle(postId,
                        expectedPostInfo.getTitle(),
                        accessToken)
                .andExpect(status().isNoContent());
        postControllerQueries.updateBody(postId,
                        expectedPostInfo.getBody(),
                        accessToken)
                .andExpect(status().isNoContent());
        postControllerQueries.updateState(postId,
                        expectedPostInfo.getState().name(),
                        accessToken)
                .andExpect(status().isNoContent());
        postControllerQueries.updateTags(postId,
                        expectedPostInfo.getTags(),
                        accessToken)
                .andExpect(status().isNoContent());
        postControllerQueries.updatePostContacts(postId,
                        new ContactList(expectedPostInfo.getPostContacts()),
                        accessToken)
                .andExpect(status().isNoContent());
        postControllerQueries.updateUserContacts(postId,
                        new ContactList(expectedPostInfo.getUserContacts()),
                        accessToken)
                .andExpect(status().isNoContent());
        var postInfo = postService.getPostInfo(postId, username);
        assertPostsEqual(postInfo, expectedPostInfo);
        assert postInfo.getPostedDate().isPresent();
        assert postInfo.getModificationDate().isPresent();
    }

    @Test
    public void postInfoTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var accessToken = loginPreconfigured(mockMvc, username);
        var postCreationData = commonPost(username);
        var postIdString = postControllerQueries.create(postCreationData, accessToken)
                .andReturn().getResponse().getContentAsString();
        var postId = Long.parseLong(postIdString);
        var postInfoJson = postControllerQueries.info(postId, accessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var postInfo = jsonToPostInfo(postInfoJson);
        assertPostsEqual(postInfo, postCreationData);
    }

    @Test
    public void allPostsTest() throws Exception {
        var userUsername = RANDOM_USER_USERNAME;
        var accessToken = loginPreconfigured(mockMvc, userUsername);
        var answerPageSize = serviceConstants.getPostsPerPage();
        var n = 3;
        var postIdsNPagesSizeList = new LinkedList<Long>();
        var templateCreationData = commonPost(userUsername);
        templateCreationData.setUserContacts(null);
        for (int pageIndex = 0; pageIndex < n; pageIndex++) {
            for (int i = 0; i < answerPageSize; i++) {
                var createdPost = postService.create(templateCreationData, userUsername);
                postIdsNPagesSizeList.add(createdPost.getId());
            }
        }
        var aggressorUsername = MODERATOR_USERNAME;
        blackListService.add(aggressorUsername, userUsername);
        var aggressorPost = postService.create(templateCreationData, aggressorUsername);
        var aggressorPostId = aggressorPost.getId();
        var gotPostIds = new LinkedList<Long>();
        for (int pageIndex = 0; pageIndex < n + 1; pageIndex++) {
            var idsListJson = postControllerQueries.all(
                            pageIndex + 1,
                            List.of("posted"),
                            accessToken)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            gotPostIds.addAll(jsonToIdsList(idsListJson).getIds());
        }
        assert listEqualsIgnoreOrder(gotPostIds, postIdsNPagesSizeList);
        assert !postIdsNPagesSizeList.contains(aggressorPostId);
    }

    @Test
    public void homeTest() throws Exception {
        var moderatorUsername = MODERATOR_USERNAME;
        var moderatorAccessToken = loginPreconfigured(mockMvc, moderatorUsername);
        var javaTagInfo = new TagInfo("java", "3 billions devices works with it...");
        var rustTagInfo = new TagInfo("rust", "Powerful language loved by millions developers");
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        var goTagInfo = new TagInfo("go", "Duck typing like in js, lol");
        // adding tags
        tagService.add(javaTagInfo.getName(), javaTagInfo.getDescription());
        tagService.add(rustTagInfo.getName(), rustTagInfo.getDescription());
        tagService.add(rubyTagInfo.getName(), rubyTagInfo.getDescription());
        tagService.add(goTagInfo.getName(), goTagInfo.getDescription());
        // setting home tags
        tagService.homeTagsUpdate(
                new LinkedList<>(List.of(javaTagInfo.getName(), rustTagInfo.getName())),
                new LinkedList<>(List.of(rubyTagInfo.getName())),
                moderatorUsername);
        var answerPageSize = serviceConstants.getPostsPerPage();
        var expectedHomePostIds = new LinkedList<Long>();
        var templateCreationData = commonPost(moderatorUsername);
        templateCreationData.setUserContacts(null);
        // black list
        var aggressorUsername = RANDOM_USER_USERNAME;
        blackListService.add(aggressorUsername, moderatorUsername);
        // not black listed user
        var friendlyUsername = ANOTHER_MODERATOR_USERNAME;
        // adding post with included tags by black listed
        templateCreationData.setTags(List.of(javaTagInfo.getName()));
        postService.create(templateCreationData, aggressorUsername);
        // adding not shown post by friendly
        templateCreationData.setTags(List.of(javaTagInfo.getName(), rubyTagInfo.getName())); // ruby excluded
        postService.create(templateCreationData, friendlyUsername);
        templateCreationData.setTags(List.of(goTagInfo.getName())); // nothing included
        postService.create(templateCreationData, friendlyUsername);
        // adding shown post by friendly
        templateCreationData.setTags(List.of(javaTagInfo.getName(), goTagInfo.getName())); // java included
        expectedHomePostIds.add(postService.create(templateCreationData, friendlyUsername).getId());
        templateCreationData.setTags(List.of(javaTagInfo.getName(), rustTagInfo.getName())); // java and rust included
        expectedHomePostIds.add(postService.create(templateCreationData, friendlyUsername).getId());
        var allPostsCount = 5;
        var pagesToReceiveCount = Math.ceil((double) allPostsCount / answerPageSize);
        var gotHomePostIds = new LinkedList<Long>();
        for (var pageIndex = 0; pageIndex < pagesToReceiveCount; pageIndex++) {
            var idsListJson = postControllerQueries.home(
                    pageIndex + 1,
                    List.of("posted"),
                    moderatorAccessToken)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            gotHomePostIds.addAll(jsonToIdsList(idsListJson).getIds());
        }
        assert listEqualsIgnoreOrder(gotHomePostIds, expectedHomePostIds);
    }

    @Test
    public void subscriptionsTest() throws Exception {
        var moderatorUsername = MODERATOR_USERNAME;
        var moderatorAccessToken = loginPreconfigured(mockMvc, moderatorUsername);
        var answerPageSize = serviceConstants.getPostsPerPage();
        var expectedSubscriptionPostIds = new LinkedList<Long>();
        var templateCreationData = commonPost(moderatorUsername);
        templateCreationData.setUserContacts(null);
        // sub to blacklisted - shown if sub
        var aggressorUsername = RANDOM_USER_USERNAME;
        blackListService.add(aggressorUsername, moderatorUsername);
        subscriptionService.subscribe(moderatorUsername, aggressorUsername);
        // not blacklisted user - not subbed to
        var friendlyUsername = ANOTHER_MODERATOR_USERNAME;
        // adding post by blacklisted (subbed)
        expectedSubscriptionPostIds.add(postService.create(templateCreationData, aggressorUsername).getId());
        expectedSubscriptionPostIds.add(postService.create(templateCreationData, aggressorUsername).getId());
        // adding post by not blacklisted (not subbed)
        postService.create(templateCreationData, friendlyUsername);
        postService.create(templateCreationData, friendlyUsername);
        postService.create(templateCreationData, friendlyUsername);
        var allPostsCount = 5;
        var pagesToReceiveCount = Math.ceil((double) allPostsCount / answerPageSize);
        var gotHomePostIds = new LinkedList<Long>();
        for (var pageIndex = 0; pageIndex < pagesToReceiveCount; pageIndex++) {
            var idsListJson = postControllerQueries.subscriptions(
                            pageIndex + 1,
                            List.of("posted"),
                            moderatorAccessToken)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            gotHomePostIds.addAll(jsonToIdsList(idsListJson).getIds());
        }
        assert listEqualsIgnoreOrder(gotHomePostIds, expectedSubscriptionPostIds);
    }

    @Test
    public void favouritesTest() throws Exception {
        var moderatorUsername = MODERATOR_USERNAME;
        var moderatorAccessToken = loginPreconfigured(mockMvc, moderatorUsername);
        var answerPageSize = serviceConstants.getPostsPerPage();
        var expectedFavouritePostIds = new LinkedList<Long>();
        var templateCreationData = commonPost(moderatorUsername);
        templateCreationData.setUserContacts(null);
        // sub to blacklisted
        var aggressorUsername = RANDOM_USER_USERNAME;
        blackListService.add(aggressorUsername, moderatorUsername);
        subscriptionService.subscribe(moderatorUsername, aggressorUsername);
        // not blacklisted user
        var friendlyUsername = ANOTHER_MODERATOR_USERNAME;
        // adding post by blacklisted (one is favourite)
        expectedFavouritePostIds.add(postService.create(templateCreationData, aggressorUsername).getId());
        favouritePostService.addPostToFavourites(expectedFavouritePostIds.getLast(), moderatorUsername);
        postService.create(templateCreationData, aggressorUsername).getId();
        // adding post by not blacklisted (one is favourite)
        expectedFavouritePostIds.add(postService.create(templateCreationData, friendlyUsername).getId());
        favouritePostService.addPostToFavourites(expectedFavouritePostIds.getLast(), moderatorUsername);
        postService.create(templateCreationData, friendlyUsername);
        postService.create(templateCreationData, friendlyUsername);
        var allPostsCount = 5;
        var pagesToReceiveCount = Math.ceil((double) allPostsCount / answerPageSize);
        var gotFavouritePostIds = new LinkedList<Long>();
        for (var pageIndex = 0; pageIndex < pagesToReceiveCount; pageIndex++) {
            var idsListJson = postControllerQueries.favourites(
                            pageIndex + 1,
                            List.of("posted"),
                            moderatorAccessToken)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            gotFavouritePostIds.addAll(jsonToIdsList(idsListJson).getIds());
        }
        assert listEqualsIgnoreOrder(gotFavouritePostIds, expectedFavouritePostIds);
    }

    public PostCreationData commonPost(String creatorUsername) {
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
        builder.tags(List.of("music", "lil-peep"));
        builder.postContacts(postContacts);
        builder.userContacts(userContacts);
        return builder.build();
    }

    public static PostCreationData commonPost(TagService tagService) {
        tagService.add("music", "Smth which lil peep invented");
        tagService.add("lil-peep", "Simply the best (imho)");
        var postContacts = List.of(new BaseContactInfo(VK, "lil-peep-fans"));
        //building
        var builder = PostCreationData.builder();
        builder.title("Lil peep listeners");
        builder.body("I want to listen lil peep music hugging with somebody :(");
        builder.state(Post.State.POSTED);
        builder.tags(List.of("music", "lil-peep"));
        builder.postContacts(postContacts);
        return builder.build();
    }

    public void assertPostsEqual(PostInfo postInfo, PostCreationData expectedPostInfo) {
        var titleEquals = postInfo.getTitle().equals(expectedPostInfo.getTitle());
        var bodyEquals = postInfo.getBody().equals(expectedPostInfo.getBody());
        var stateEquals = postInfo.getState() == expectedPostInfo.getState();
        var tagNamesEqual = listEqualsIgnoreOrder(
                postInfo.getTags(),
                expectedPostInfo.getTags());
        var postContactsEqual = (postInfo.getPostContacts().isEmpty() && expectedPostInfo.getPostContacts().isEmpty()) ||
                listEqualsIgnoreOrder(
                        postInfo.getPostContacts(),
                        expectedPostInfo.getPostContacts());
        var postUserContactsEqual = (postInfo.getPostUserContacts().isEmpty() && expectedPostInfo.getUserContacts().isEmpty()) ||
                listEqualsIgnoreOrder(
                        postInfo.getPostUserContacts(),
                        expectedPostInfo.getUserContacts());
        assert tagNamesEqual;
        assert titleEquals;
        assert bodyEquals;
        assert stateEquals;
        assert postContactsEqual;
        assert postUserContactsEqual;
    }

    public static PostInfo jsonToPostInfo(String postInfoJson) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.readValue(postInfoJson, PostInfo.class);
    }

    public static IdsList jsonToIdsList(String postInfoJson) throws JsonProcessingException {
        return new ObjectMapper().readValue(postInfoJson, IdsList.class);
    }
}
