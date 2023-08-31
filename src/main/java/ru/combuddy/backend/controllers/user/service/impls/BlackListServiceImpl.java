package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.service.interfaces.BlackListService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.exceptions.permission.user.SelfActionException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.user.BlackListRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private BlackListRepository blackListRepository;
    private UserAccountService userAccountService;

    @Override
    public void add(String aggressorUsername, String defendedUsername)
            throws UserNotExistsException,
            SelfActionException {
        var aggressor = userAccountService.getByUsername(aggressorUsername);
        var defended = userAccountService.getByUsername(defendedUsername);
        if (aggressor.equals(defended)) {
            throw new SelfActionException("Can not add this to black list");
        }
        if (!this.exists(aggressor.getId(), defended.getId())) {
            blackListRepository.save(new BlackList(null, aggressor, defended));
        }
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

    @Override
    public boolean exists(Long aggressorId, Long defendedId) {
        return blackListRepository.existsByAggressorIdAndDefendedId(
                aggressorId,
                defendedId);
    }
}
