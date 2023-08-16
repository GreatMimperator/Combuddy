package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.models.User;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.repositories.user.UserAccountRepository;
import ru.combuddy.backend.repositories.user.UserInfoRepository;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.repositories.RoleRepository;

import java.util.*;
import java.util.function.Function;

@Service
@Transactional
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public User createUser(User user) throws AlreadyExistsException {
        var username = user.getUserAccount().getUsername();
        var foundUserAccount = userAccountRepository.findByUsername(username);
        if (foundUserAccount.isPresent()) {
            throw new AlreadyExistsException("User account with username %s already exists".formatted(username));
        }
        var userAccount = userAccountRepository.save(user.getUserAccount());
        user.getUserInfo().setUserAccount(userAccount);
        var userInfo = userInfoRepository.save(user.getUserInfo());
        return new User(userAccount, userInfo);
    }

    @Override
    public UserAccount createDefaultUser(String username) throws AlreadyExistsException {
        if (userAccountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException("User with username %s already exists".formatted(username));
        }
        var userAccount = new UserAccount(username);
        var roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("ROLE_USER").get());
        userAccount.setRoles(roles);
        return userAccountRepository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public boolean updateFrozenState(boolean frozen, String username) {
        var foundUserAccount = userAccountRepository.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            return false;
        }
        var userAccount = foundUserAccount.get();
        userAccount.setFrozen(frozen);
        userAccountRepository.save(userAccount);
        return true;
    }

    @Override
    public boolean exists(String username) {
        return userAccountRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean delete(String username) {
        return userAccountRepository.deleteByUsername(username) == 1;
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
}
