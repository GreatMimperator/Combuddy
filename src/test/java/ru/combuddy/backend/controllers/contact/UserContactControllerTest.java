package ru.combuddy.backend.controllers.contact;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.queries.contact.UserContactControllerQueries;
import ru.combuddy.backend.queries.user.UserInfoControllerQueries;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;
import static ru.combuddy.backend.controllers.user.UserInfoControllerTest.jsonToUserProfileInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserContactControllerQueries userContactControllerQueries;

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void addRemoveTest() throws Exception {
        var username = RANDOM_USER_USERNAME;
        var loginResponse = loginPreconfigured(mockMvc, username);
        var vkContactInfo = new BaseContactInfo(ContactType.VK, "moriarty");
        userContactControllerQueries.add(
                vkContactInfo.getContactType().name(),
                vkContactInfo.getValue(),
                loginResponse.getAccessToken())
                .andExpect(status().isCreated());
        var telegramContactInfo = new BaseContactInfo(ContactType.TELEGRAM, "great_moriarty");
        userContactControllerQueries.add(
                        telegramContactInfo.getContactType().name(),
                        telegramContactInfo.getValue(),
                        loginResponse.getAccessToken())
                .andExpect(status().isCreated());
        var userProfileInfo = userInfoService.getAllInfo(username, username);
        assert userProfileInfo.getContacts().equals(Set.of(vkContactInfo, telegramContactInfo));
        userContactControllerQueries.remove(
                        vkContactInfo.getContactType().name(),
                        vkContactInfo.getValue(),
                        loginResponse.getAccessToken())
                .andExpect(status().isNoContent());;
        userProfileInfo = userInfoService.getAllInfo(username, username);
        assert userProfileInfo.getContacts().equals(Set.of(telegramContactInfo));
        userContactControllerQueries.remove(
                        telegramContactInfo.getContactType().name(),
                        telegramContactInfo.getValue(),
                        loginResponse.getAccessToken())
                .andExpect(status().isNoContent());
        userProfileInfo = userInfoService.getAllInfo(username, username);
        assert userProfileInfo.getContacts().equals(Set.of());
    }
}
