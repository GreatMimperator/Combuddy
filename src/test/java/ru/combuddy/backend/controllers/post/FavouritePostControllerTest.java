package ru.combuddy.backend.controllers.post;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.post.service.interfaces.PostService;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.queries.post.FavouritePostControllerQueries;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.Util.listEqualsIgnoreOrder;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FavouritePostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavouritePostControllerQueries favouritePostControllerQueries;

    @Autowired
    private PostService postService;
    @Autowired
    private TagService tagService;

    @Test
    public void addDeleteTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var userAccessToken = loginPreconfigured(mockMvc, username);
        var creatorUsername = MODERATOR_USERNAME;
        var commonPost = PostControllerTest.commonPost(tagService);
        postService.create(commonPost, creatorUsername);
        var favouritePostId = postService.create(commonPost, creatorUsername).getId();
        postService.create(commonPost, creatorUsername);
        assert postService.searchFavourites(1, List.of("posted"), username).isEmpty();
        favouritePostControllerQueries.add(favouritePostId, userAccessToken)
                .andExpect(status().isNoContent());
        assert listEqualsIgnoreOrder(
                postService.searchFavourites(1, List.of("posted"), username),
                List.of(favouritePostId));
        favouritePostControllerQueries.delete(favouritePostId, userAccessToken)
                .andExpect(status().isNoContent());
        assert postService.searchFavourites(1, List.of("posted"), username).isEmpty();
    }
}
