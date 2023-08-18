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

import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernames;
import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernamesWithAskerExistenceCheck;

@Service
@Transactional
@AllArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private BlackListRepository blackListRepository;
    private UserAccountService userAccountRepository;

    @Override
    public void add(String aggressorUsername, String defendedUsername) throws ShouldNotBeEqualException, NotExistsException {
        usernamesEqualCheck(aggressorUsername, defendedUsername);
        var foundAggressor = userAccountRepository.findByUsername(aggressorUsername);
        var foundDefended = userAccountRepository.findByUsername(defendedUsername);
        foundCheck(foundAggressor, aggressorUsername, foundDefended, defendedUsername);
        var blackListRecord = new BlackList(null,
                foundAggressor.get(),
                foundDefended.get());
        var aggressorId = blackListRecord.getAggressor().getId();
        var defendedId = blackListRecord.getDefended().getId();
        if (blackListRepository.existsByAggressorIdAndDefendedId(aggressorId, defendedId)) {
            return;
        }
        blackListRepository.save(blackListRecord);
    }

    @Override
    public void remove(String aggressorUsername, String defendedUsername) throws ShouldNotBeEqualException, NotExistsException {
        usernamesEqualCheck(aggressorUsername, defendedUsername);
        var foundAggressor = userAccountRepository.findByUsername(aggressorUsername);
        var foundDefended = userAccountRepository.findByUsername(defendedUsername);
        foundCheck(foundAggressor, aggressorUsername, foundDefended, defendedUsername);
        var aggressorId = foundAggressor.get().getId();
        var defendedId = foundDefended.get().getId();
        blackListRepository.deleteByDefendedIdAndAggressorId(defendedId, aggressorId);
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

    /**
     * @throws NotExistsException if aggressor or defended are empty
     */
    private void foundCheck(Optional<UserAccount> foundAggressor, String aggressorQueryUsername,
                            Optional<UserAccount> foundDefended, String defendedQueryUsername)
            throws NotExistsException {
        if (foundAggressor.isEmpty()) {
            throw new NotExistsException(
                    MessageFormat.format("Aggressor {0} doesn't exist",
                            aggressorQueryUsername));
        }
        if (foundDefended.isEmpty()) {
            throw new NotExistsException(
                    MessageFormat.format("Defended {0} doesn't exist",
                            defendedQueryUsername));
        }
    }

    @Override
    public List<String> getAggressorUsernames(String defendedUsername) {
        var aggressorUsernames = blackListRepository
                .findAggressorUsernamesByDefendedUsername(defendedUsername);
        return aggressorUsernames.stream()
                .map(r -> r.getAggressor().getUsername())
                .toList();
    }
}
