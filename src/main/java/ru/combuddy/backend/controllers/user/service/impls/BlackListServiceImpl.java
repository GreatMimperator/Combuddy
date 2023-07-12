package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.repositories.user.BlackListRepository;

import java.util.List;
import java.util.Optional;

import static ru.combuddy.backend.controllers.user.service.impls.UserAccountServiceImpl.getUsernamesWithAskerExistenceCheck;

@Service
@Transactional
@AllArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private BlackListRepository blackListRepository;
    private UserAccountService userAccountRepository;

    @Override
    public Optional<List<String>> getAggressorUsernames(String defendedUsername) {
        var aggressorUsernames = blackListRepository
                .findAggressorUsernamesByDefendedUsername(defendedUsername);
        return getUsernamesWithAskerExistenceCheck(r -> r.getAggressor().getUsername(),
                aggressorUsernames,
                defendedUsername,
                userAccountRepository);
    }

    @Override
    public boolean add(String aggressorUsername, String defendedUsername) {
        var foundAggressor = userAccountRepository.findByUsername(aggressorUsername);
        var foundDefended = userAccountRepository.findByUsername(defendedUsername);
        if (foundAggressor.isEmpty() || foundDefended.isEmpty()) {
            return false;
        }
        var blackListRecord = new BlackList(null,
                foundAggressor.get(),
                foundDefended.get());
        var aggressorId = blackListRecord.getAggressor().getId();
        var defendedId = blackListRecord.getDefended().getId();
        if (blackListRepository.existsByAggressorIdAndDefendedId(aggressorId, defendedId)) {
            return true;
        }
        blackListRepository.save(blackListRecord);
        return true;
    }

    @Override
    public boolean remove(String aggressorUsername, String defendedUsername) {
        var foundAggressor = userAccountRepository.findByUsername(aggressorUsername);
        var foundDefended = userAccountRepository.findByUsername(defendedUsername);
        if (foundAggressor.isEmpty() || foundDefended.isEmpty()) {
            return false;
        }
        var aggressorId = foundAggressor.get().getId();
        var defendedId = foundDefended.get().getId();
        blackListRepository.deleteByDefendedIdAndAggressorId(defendedId, aggressorId);
        return true;
    }
}
