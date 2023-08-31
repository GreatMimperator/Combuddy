package ru.combuddy.backend.controllers.contact;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.combuddy.backend.Util;
import ru.combuddy.backend.controllers.contact.models.BaseContactInfo;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.contact.BaseContact.ContactType;
import ru.combuddy.backend.queries.contact.UserContactControllerQueries;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.combuddy.backend.controllers.user.AuthControllerTest.*;

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
        var accessToken = loginPreconfigured(mockMvc, username);
        var vkContactInfo = new BaseContactInfo(ContactType.VK, "moriarty");
        userContactControllerQueries.add(vkContactInfo.getContactType().name(),
                        vkContactInfo.getValue(),
                        accessToken)
                .andExpect(status().isCreated());
        var telegramContactInfo = new BaseContactInfo(ContactType.TELEGRAM, "great_moriarty");
        userContactControllerQueries.add(telegramContactInfo.getContactType().name(),
                        telegramContactInfo.getValue(),
                        accessToken)
                .andExpect(status().isCreated());
        var userProfileInfo = userInfoService.getAllInfo(username, username);
        assert Util.listEqualsIgnoreOrder(userProfileInfo.getContacts(), List.of(vkContactInfo, telegramContactInfo));
        userContactControllerQueries.delete(vkContactInfo.getContactType().name(),
                        vkContactInfo.getValue(),
                        accessToken)
                .andExpect(status().isNoContent());
        userProfileInfo = userInfoService.getAllInfo(username, username);
        assert Util.listEqualsIgnoreOrder(userProfileInfo.getContacts(), List.of(telegramContactInfo));
        userContactControllerQueries.delete(telegramContactInfo.getContactType().name(),
                        telegramContactInfo.getValue(),
                        accessToken)
                .andExpect(status().isNoContent());
        userProfileInfo = userInfoService.getAllInfo(username, username);
        assert userProfileInfo.getContacts().isEmpty();
    }
}
