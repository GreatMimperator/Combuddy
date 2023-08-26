package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.exceptions.ShouldNotBeEqualException;
import ru.combuddy.backend.repositories.user.BlackListRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private BlackListRepository blackListRepository;
    private UserAccountService userAccountService;

    @Transactional
    @Override
    public void add(String aggressorUsername, String defendedUsername)
            throws ShouldNotBeEqualException, NotExistsException {
        usernamesEqualCheck(aggressorUsername, defendedUsername);
        var aggressor = userAccountService.getByUsername(aggressorUsername, "aggressor");
        var defended = userAccountService.getByUsername(defendedUsername, "defended");
        if (blackListRepository.existsByAggressorIdAndDefendedId(aggressor.getId(), defended.getId())) {
            return;
        }
        blackListRepository.save(new BlackList(null, aggressor, defended));
    }

    @Override
    public boolean delete(String aggressorUsername, String defendedUsername) {
        var deletedCount = blackListRepository.deleteByAggressorUsernameAndDefendedUsername(
                aggressorUsername,
                defendedUsername);
        return deletedCount > 0;
    }

    @Transactional
    @Override
    public List<String> getAggressorUsernames(String defendedUsername) {
        var aggressorUsernames = blackListRepository
                .findAggressorUsernamesByDefendedUsername(defendedUsername);
        return aggressorUsernames.stream()
                .map(r -> r.getAggressor().getUsername())
                .toList();
    }

    @Override
    public Optional<BlackList> findRecord(String aggressorUsername, String defendedUsername) {
        return blackListRepository.findByAggressorUsernameAndDefendedUsername(
                aggressorUsername,
                defendedUsername);
    }

    /**
     * @throws ShouldNotBeEqualException if aggressor and defended usernames are equal
     */
    private void usernamesEqualCheck(String aggressorUsername, String defendedUsername)
            throws ShouldNotBeEqualException {
        if (aggressorUsername.equals(defendedUsername)) {
            throw new ShouldNotBeEqualException(
                    MessageFormat.format("aggressor and defended usernames are equal ({0})",
                            aggressorUsername));
        }
    }
}
