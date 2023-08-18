package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.entities.user.UserRole;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.user.UserAccountRepository;
import ru.combuddy.backend.repositories.user.UserRoleRepository;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.security.repositories.RoleRepository;

import java.util.*;
import java.util.function.Function;

@Service
@Transactional
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public UserAccount createDefaultUser(String username) throws AlreadyExistsException {
        if (userAccountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException("User account with username %s already exists".formatted(username));
        }
        var userAccount = new UserAccount(username);
        var userRole = roleRepository.findByName("ROLE_USER").get();
        var userAccountRole = userRoleRepository.save(new UserRole(null, userAccount, userRole));
        userAccount.setUserRoles(Set.of(userAccountRole));
        var userInfo = userInfoRepository.save(new UserInfo(userAccount));
        userAccount.setUserInfo(userInfo);
        return userAccountRepository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public void updateFrozenState(boolean frozen, String username) throws NotExistsException {
        var foundUserAccount = userAccountRepository.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throw new NotExistsException("User account with username %s doesn't exist, so can't be frozen"
                    .formatted(username));
        }
        var userAccount = foundUserAccount.get();
        userAccount.setFrozen(frozen);
        userAccountRepository.save(userAccount);
    }

    @Override
    public boolean exists(String username) {
        return userAccountRepository.findByUsername(username).isPresent();
    }

    @Override
    public void delete(String username) throws NotExistsException {
        if (userAccountRepository.deleteByUsername(username) == 0) {
            throw new NotExistsException("User account with username %s doesn't exist, so can't be frozen"
                    .formatted(username));
        }
    }

    @Override
    public List<String> findUsernamesStartedWith(String usernameBeginPart) {
        return userAccountRepository.findByUsernameStartingWith(usernameBeginPart).stream()
                .map(UsernameOnlyUserAccountProjection::getUsername)
                .toList();
    }

    @Override
    public boolean isFrozen(String username) {
        return userAccountRepository.findFrozenByUsername(username).getFrozen();
    }

    @Deprecated
    public static <Returned>Optional<List<String>> getUsernamesWithAskerExistenceCheck(
            Function<Returned, String> returnedToUsernameConverter,
            List<Returned> returnedList,
            String askerUsername,
            UserAccountService userAccountRepository) {
        var usernamesList = returnedList.stream()
                .map(returnedToUsernameConverter)
                .toList();
        if (usernamesList.isEmpty() && !userAccountRepository.exists(askerUsername)) {
            return Optional.empty();
        }
        return Optional.of(usernamesList);
    }

    @Deprecated
    public static <Returned>List<String> getUsernames(
            Function<Returned, String> returnedToUsernameConverter,
            List<Returned> returnedList) {
        return returnedList.stream()
                .map(returnedToUsernameConverter)
                .toList();
    }
}
