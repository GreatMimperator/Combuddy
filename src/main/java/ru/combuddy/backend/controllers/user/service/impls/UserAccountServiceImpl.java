package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.user.projections.account.RoleOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.AlreadyExistsException;
import ru.combuddy.backend.exceptions.NotExistsException;
import ru.combuddy.backend.repositories.user.UserAccountRepository;
import ru.combuddy.backend.security.entities.Role;
import ru.combuddy.backend.security.repositories.RoleRepository;

import java.text.MessageFormat;
import java.util.*;

import static ru.combuddy.backend.security.entities.Role.RoleName.ROLE_USER;

@Service
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserAccount createDefaultUser(String username) throws AlreadyExistsException {
        if (userAccountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException(
                    MessageFormat.format("User account with username {0} already exists",
                            username),
                    username);
        }
        var userAccount = new UserAccount();
        userAccount.setUsername(username);
        var role = roleRepository.findByName(ROLE_USER).get();
        userAccount.setRole(role);
        var userInfo = new UserInfo(userAccount);
        userAccount.setUserInfo(userInfo);
        var privacyPolicy = new PrivacyPolicy(userAccount);
        userAccount.setPrivacyPolicy(privacyPolicy);
        return userAccountRepository.save(userAccount);
    }

    @Override
    public boolean exists(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }


    @Override
    public Optional<Role> findRoleByUsername(String username) {
        return userAccountRepository.findRoleByUsername(username)
                .map(RoleOnlyUserAccountProjection::getRole);
    }

    @Transactional
    @Override
    public void updateFrozenState(boolean frozen, String username) throws NotExistsException {
        var foundUserAccount = userAccountRepository.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throwUserNotExists(username, "frozen candidate");
        }
        var userAccount = foundUserAccount.get();
        userAccount.setFrozen(frozen);
        userAccountRepository.save(userAccount);
    }

    @Override
    public boolean delete(String username) {
        var deletedCount = userAccountRepository.deleteByUsername(username);
        return deletedCount > 0;
    }

    @Override
    public List<String> findUsernamesStartedWith(String usernameBeginPart) {
        return userAccountRepository.findByUsernameStartingWith(usernameBeginPart).stream()
                .map(UsernameOnlyUserAccountProjection::getUsername)
                .toList();
    }

    @Override
    public boolean isFrozen(String username) throws NotExistsException {
        var foundFrozen = userAccountRepository.findFrozenByUsername(username);
        if (foundFrozen.isEmpty()) {
            throwUserNotExists(username, "frozen check candidate");
        }
        return foundFrozen.get().getFrozen();
    }

    @Override
    public void replaceRole(UserAccount receiver, Role.RoleName roleName) throws NotExistsException {
        receiver.setRole(roleRepository.findByName(roleName).get());
        userAccountRepository.save(receiver);
    }
}
