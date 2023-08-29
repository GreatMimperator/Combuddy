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
        var loginResponse = loginPreconfigured(mockMvc, MAIN_MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful language loved by millions developers");
        tagControllerQueries.add(rustTagInfo.getName(),
                        rustTagInfo.getDescription(),
                        loginResponse.getAccessToken()) // todo: change new line style everywhere in tests (press enter again - idea will make more tabs if after has calls)
                .andExpect(status().isCreated());
        var javaTagInfo = new TagInfo("java", "3 billions devices works with it...");
        tagControllerQueries.add(javaTagInfo.getName(),
                        javaTagInfo.getDescription(),
                        loginResponse.getAccessToken())
                .andExpect(status().isCreated());
        var expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        var tagList = new ArrayList<>(List.of(rustTagInfo, javaTagInfo));
        assert tagList.containsAll(expectedTagList) && expectedTagList.containsAll(tagList);
        tagControllerQueries.remove(javaTagInfo.getName(),
                        loginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        tagList.remove(javaTagInfo);
        assert tagList.containsAll(expectedTagList) && expectedTagList.containsAll(tagList);
        tagControllerQueries.remove(rustTagInfo.getName(),
                        loginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        expectedTagList = tagService.getAll().stream().map(TagInfo::new).toList();
        tagList.remove(rustTagInfo);
        assert tagList.containsAll(expectedTagList) && expectedTagList.containsAll(tagList);
    }

    @Test
    public void changeDescriptionTest() throws Exception {
        var userLoginResponse = loginPreconfigured(mockMvc, RANDOM_USER_USERNAME);
        tagControllerQueries.changeDescription("anything",
                "description",
                userLoginResponse.getAccessToken())
                .andExpect(status().isForbidden());
        var moderatorLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful................");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        rustTagInfo.setDescription("Maybe powerful..................");
        tagControllerQueries.changeDescription(rustTagInfo.getName(),
                        rustTagInfo.getDescription(),
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        var expectedTagInfo = new TagInfo(tagService.find(rustTagInfo.getName()).get());
        assert rustTagInfo.equals(expectedTagInfo);
    }

    @Test
    public void getBeginWithTest() throws Exception {
        var moderatorLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var tagsBeginWithRuJson = tagControllerQueries.namesBeginWith("ru",
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagsBeginWithRu = jsonToTagNames(tagsBeginWithRuJson).getTagNames();
        var expectedTagsBeginWithRu = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        assert tagsBeginWithRu.containsAll(expectedTagsBeginWithRu) &&
                expectedTagsBeginWithRu.containsAll(tagsBeginWithRu);
        var tagsBeginWithJJson = tagControllerQueries.namesBeginWith("j",
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagsBeginWithJ = jsonToTagNames(tagsBeginWithJJson).getTagNames();
        var expectedTagsBeginWithJ = List.of(javaTagInfo.getName());
        assert tagsBeginWithJ.containsAll(expectedTagsBeginWithJ) && // todo: maybe i'll replace it with static method...
                expectedTagsBeginWithJ.containsAll(tagsBeginWithJ);
    }

    @Test
    public void getAllTest() throws Exception {
        var moderatorLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var tagNamesAllTagsJson = tagControllerQueries.namesAll(
                moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        var expectedTagNamesAll = List.of(rustTagInfo.getName());
        assert tagNamesAllTags.containsAll(expectedTagNamesAll) &&
                expectedTagNamesAll.containsAll(tagNamesAllTags);
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        tagNamesAllTagsJson = tagControllerQueries.namesAll(
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        expectedTagNamesAll = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        assert tagNamesAllTags.containsAll(expectedTagNamesAll) &&
                expectedTagNamesAll.containsAll(tagNamesAllTags);
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        tagNamesAllTagsJson = tagControllerQueries.namesAll(
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        tagNamesAllTags = jsonToTagNames(tagNamesAllTagsJson).getTagNames();
        expectedTagNamesAll = List.of(rustTagInfo.getName(),
                rubyTagInfo.getName(),
                javaTagInfo.getName());
        assert tagNamesAllTags.containsAll(expectedTagNamesAll) &&
                expectedTagNamesAll.containsAll(tagNamesAllTags);
    }

    @Test
    public void getDescriptionTest() throws Exception {
        var moderatorLoginResponse = loginPreconfigured(mockMvc, MODERATOR_USERNAME);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var rustTagDescription = tagControllerQueries.description(rustTagInfo.getName(),
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assert rustTagDescription.equals(rustTagInfo.getDescription());
        var rubyTagDescription = tagControllerQueries.description(rubyTagInfo.getName(),
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assert rubyTagDescription.equals(rubyTagInfo.getDescription());
    }

    @Test
    public void receiveAndUpdateHomeTagsTest() throws Exception {
        var username = MODERATOR_USERNAME;
        var moderatorLoginResponse = loginPreconfigured(mockMvc, username);
        var rustTagInfo = new TagInfo("rust", "Powerful........");
        tagControllerQueries.add(rustTagInfo.getName(),
                rustTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var rubyTagInfo = new TagInfo("ruby", "Idk right?.....");
        tagControllerQueries.add(rubyTagInfo.getName(),
                rubyTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var javaTagInfo = new TagInfo("java", "3 billions btw....");
        tagControllerQueries.add(javaTagInfo.getName(),
                javaTagInfo.getDescription(),
                moderatorLoginResponse.getAccessToken());
        var includedHomeTagNames = List.of(rustTagInfo.getName(), javaTagInfo.getName());
        var excludedHomeTagNames = List.of(rubyTagInfo.getName());
        tagControllerQueries.homeTagUpdate(includedHomeTagNames,
                        excludedHomeTagNames,
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk());
        var homeFilterTags = tagService.getHomeTags(username);
        assert homeFilterTags.getIncludeTagNames().containsAll(includedHomeTagNames) &&
                includedHomeTagNames.containsAll(homeFilterTags.getIncludeTagNames());
        assert homeFilterTags.getExcludeTagNames().containsAll(excludedHomeTagNames) &&
                excludedHomeTagNames.containsAll(homeFilterTags.getExcludeTagNames());
        var gotFilterTagsJson = tagControllerQueries.homeTagGet(moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var gotFilterTags = jsonToFilterTags(gotFilterTagsJson);
        assert gotFilterTags.getIncludeTagNames().containsAll(includedHomeTagNames) &&
                includedHomeTagNames.containsAll(gotFilterTags.getIncludeTagNames());
        assert gotFilterTags.getExcludeTagNames().containsAll(excludedHomeTagNames) &&
                excludedHomeTagNames.containsAll(gotFilterTags.getExcludeTagNames());
        includedHomeTagNames = List.of(javaTagInfo.getName());
        excludedHomeTagNames = List.of(rustTagInfo.getName(), rubyTagInfo.getName());
        tagControllerQueries.homeTagUpdate(includedHomeTagNames,
                        excludedHomeTagNames,
                        moderatorLoginResponse.getAccessToken())
                .andExpect(status().isOk());
        homeFilterTags = tagService.getHomeTags(username);
        assert homeFilterTags.getIncludeTagNames().containsAll(includedHomeTagNames) &&
                includedHomeTagNames.containsAll(homeFilterTags.getIncludeTagNames());
        assert homeFilterTags.getExcludeTagNames().containsAll(excludedHomeTagNames) &&
                excludedHomeTagNames.containsAll(homeFilterTags.getExcludeTagNames());
    }

    public static FilterTags jsonToFilterTags(String filterTagsJson) throws Exception {
        return new ObjectMapper().readValue(filterTagsJson, FilterTags.class);
    }

    public static TagNames jsonToTagNames(String usernamesListJson) throws Exception {
        return new ObjectMapper().readValue(usernamesListJson, TagNames.class);
    }
}
