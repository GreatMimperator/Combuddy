package ru.combuddy.backend.controllers.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.post.models.FilterTags;
import ru.combuddy.backend.controllers.post.models.TagInfo;
import ru.combuddy.backend.controllers.post.models.TagNames;
import ru.combuddy.backend.controllers.post.service.interfaces.TagService;
import ru.combuddy.backend.queries.post.TagControllerQueries;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.Util.listEqualsIgnoreOrder;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagControllerQueries tagControllerQueries;

    @Autowired
    private TagService tagService;

    @Test
    public void addRemoveTest() throws Exception {
        var accessToken = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful language loved by millions developers");
        tagControllerQueries.add(rustTagInfo.getName(),
                        rustTagInfo.getDescription(),
                        accessToken) // todo: change new line style everywhere in tests (press enter again - idea will make more tabs if after has calls)
                .andExpect(status().isCreated());
        var javaTagInfo = new TagInfo("java", "3 billions devices works with it...");
        tagControllerQueries.add(javaTagInfo.getName(),
                        javaTagInfo.getDescription(),
                        accessToken)
                .andExpect(status().isCreated());
        var expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        var tagList = new ArrayList<>(List.of(rustTagInfo, javaTagInfo));
        assert listEqualsIgnoreOrder(tagList, expectedTagList);
        tagControllerQueries.delete(javaTagInfo.getName(),
                        accessToken)
                .andExpect(status().isNoContent());
        expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        tagList.remove(javaTagInfo);
        assert listEqualsIgnoreOrder(tagList, expectedTagList);
        tagControllerQueries.delete(rustTagInfo.getName(),
                        accessToken)
                .andExpect(status().isNoContent());
        expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        tagList.remove(rustTagInfo);
        assert listEqualsIgnoreOrder(tagList, expectedTagList);
    }

    @Test
    public void changeDescriptionTest() throws Exception {
        var userAccessToken = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        tagControllerQueries.changeDescription("anything",
                "description",
                userAccessToken)
                .andExpect(status().isForbidden());
        var moderatorAccessToken = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful................");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorAccessToken);
        rustTagInfo.setDescription("Maybe powerful..................");
        tagControllerQueries.changeDescription(rustTagInfo.getName(),
                        rustTagInfo.getDescription(),
                        moderatorAccessToken)
                .andExpect(status().isNoContent());
        var expectedTagInfo = new TagInfo(tagService.find(rustTagInfo.getName()).get());
        assert rustTagInfo.equals(expectedTagInfo);
    }

    @Test
    public void getBeginWithTest() throws Exception {
        var moderatorAccessToken = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorAccessToken);
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorAccessToken);
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorAccessToken);
        var tagsBeginWithRuJson = tagControllerQueries.namesBeginWith("ru", moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagsBeginWithRu = jsonToTagNames(tagsBeginWithRuJson).getTagNames();
        var expectedTagsBeginWithRu = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        assert listEqualsIgnoreOrder(tagsBeginWithRu, expectedTagsBeginWithRu);
        var tagsBeginWithJJson = tagControllerQueries.namesBeginWith("j", moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagsBeginWithJ = jsonToTagNames(tagsBeginWithJJson).getTagNames();
        var expectedTagsBeginWithJ = List.of(javaTagInfo.getName());
        assert listEqualsIgnoreOrder(tagsBeginWithJ, expectedTagsBeginWithJ);
    }

    @Test
    public void getAllTest() throws Exception {
        var moderatorAccessToken = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorAccessToken);
        var tagNamesAllTagsJson = tagControllerQueries.namesAll(moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        var expectedTagNamesAll = List.of(rustTagInfo.getName());
        assert listEqualsIgnoreOrder(tagNamesAllTags, expectedTagNamesAll);
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorAccessToken);
        tagNamesAllTagsJson = tagControllerQueries.namesAll(moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        expectedTagNamesAll = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        assert listEqualsIgnoreOrder(tagNamesAllTags, expectedTagNamesAll);
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorAccessToken);
        tagNamesAllTagsJson = tagControllerQueries.namesAll(moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        expectedTagNamesAll = List.of(rustTagInfo.getName(),
                rubyTagInfo.getName(),
                javaTagInfo.getName());
        assert listEqualsIgnoreOrder(tagNamesAllTags, expectedTagNamesAll);
    }

    @Test
    public void getDescriptionTest() throws Exception {
        var moderatorAccessToken = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorAccessToken);
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorAccessToken);
        var rustTagDescription = tagControllerQueries.description(rustTagInfo.getName(),
                        moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assert rustTagDescription.equals(rustTagInfo.getDescription());
        var rubyTagDescription = tagControllerQueries.description(rubyTagInfo.getName(),
                        moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assert rubyTagDescription.equals(rubyTagInfo.getDescription());
    }

    @Test
    public void receiveAndUpdateHomeTagsTest() throws Exception {
        var username = MODERATOR_USERNAME;
        var moderatorAccessToken = loginPreconfigured(mockMvc, username);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorAccessToken);
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorAccessToken);
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorAccessToken);
        var includedHomeTagNames = List.of(rustTagInfo.getName(), javaTagInfo.getName());
        var excludedHomeTagNames = List.of(rubyTagInfo.getName());
        tagControllerQueries.homeTagUpdate(includedHomeTagNames,
                        excludedHomeTagNames,
                        moderatorAccessToken)
                .andExpect(status().isNoContent());
        var homeFilterTags = tagService.getFilterTags(username);
        assert listEqualsIgnoreOrder(homeFilterTags.getIncludeTagNames(), includedHomeTagNames);
        assert listEqualsIgnoreOrder(homeFilterTags.getExcludeTagNames(), excludedHomeTagNames);
        var gotFilterTagsJson = tagControllerQueries.homeTagGet(moderatorAccessToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var gotFilterTags = jsonToFilterTags(gotFilterTagsJson);
        assert listEqualsIgnoreOrder(gotFilterTags.getIncludeTagNames(), includedHomeTagNames);
        assert listEqualsIgnoreOrder(gotFilterTags.getExcludeTagNames(), excludedHomeTagNames);
        includedHomeTagNames = List.of(javaTagInfo.getName());
        excludedHomeTagNames = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        tagControllerQueries.homeTagUpdate(includedHomeTagNames,
                        excludedHomeTagNames,
                        moderatorAccessToken)
                .andExpect(status().isNoContent());
        homeFilterTags = tagService.getFilterTags(username);
        assert listEqualsIgnoreOrder(homeFilterTags.getIncludeTagNames(), includedHomeTagNames);
        assert listEqualsIgnoreOrder(homeFilterTags.getExcludeTagNames(), excludedHomeTagNames);
    }

    public static FilterTags jsonToFilterTags(String filterTagsJson) throws Exception {
        return new ObjectMapper().readValue(filterTagsJson, FilterTags.class);
    }

    public static TagNames jsonToTagNames(String usernamesListJson) throws Exception {
        return new ObjectMapper().readValue(usernamesListJson, TagNames.class);
    }
}
