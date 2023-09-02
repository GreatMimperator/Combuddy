package ru.combuddy.backend.controllers.user.service.impls;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.controllers.ServiceConstants;
import ru.combuddy.backend.controllers.user.projections.account.UsernameOnlyUserAccountProjection;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.controllers.user.service.interfaces.UserInfoService;
import ru.combuddy.backend.entities.user.PrivacyPolicy;
import ru.combuddy.backend.entities.user.UserAccount;
import ru.combuddy.backend.entities.user.UserInfo;
import ru.combuddy.backend.exceptions.general.IllegalPageNumberException;
import ru.combuddy.backend.exceptions.permission.DeleteNotPermittedException;
import ru.combuddy.backend.exceptions.permission.FreezeStateSetNotPermittedException;
import ru.combuddy.backend.exceptions.permission.user.RoleSetNotPermittedException;
import ru.combuddy.backend.exceptions.user.InvalidRoleNameException;
import ru.combuddy.backend.exceptions.user.UserAlreadyExistsException;
import ru.combuddy.backend.exceptions.user.UserNotExistsException;
import ru.combuddy.backend.repositories.user.UserAccountRepository;
import ru.combuddy.backend.security.AuthorityComparator;
import ru.combuddy.backend.security.RoleName;
import ru.combuddy.backend.security.verifiers.users.UsersDeleteVerifier;
import ru.combuddy.backend.security.verifiers.users.UsersFreezeVerifier;
import ru.combuddy.backend.security.verifiers.users.role.RoleDecreaseVerifier;
import ru.combuddy.backend.security.verifiers.users.role.RoleIncreaseVerifier;

import java.util.*;

import static ru.combuddy.backend.controllers.ServiceConstants.checkPageNumber;
import static ru.combuddy.backend.security.RoleName.ROLE_USER;

@Service
@Transactional
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    private final UsersDeleteVerifier deleteVerifier;
    private final UsersFreezeVerifier freezeVerifier;
    private final RoleIncreaseVerifier roleIncreaseVerifier;
    private final RoleDecreaseVerifier roleDecreaseVerifier;

    private final AuthorityComparator authorityComparator;

    public final ServiceConstants serviceConstants;

    @Override
    public UserAccount createDefaultUser(String username) throws UserAlreadyExistsException {
        if (this.exists(username)) {
            throw new UserAlreadyExistsException("User already exists, so can not create with this username");
        }
        var userAccount = new UserAccount();
        userAccount.setUsername(username);
        userAccount.setRoleName(ROLE_USER);
        var userInfo = new UserInfo(userAccount);
        userAccount.setUserInfo(userInfo);
        var privacyPolicy = new PrivacyPolicy(userAccount);
        UserInfoService.setPrivacyPolicyToDefault(privacyPolicy);
        userAccount.setPrivacyPolicy(privacyPolicy);
        return userAccountRepository.save(userAccount);
    }

    @Override
    public boolean exists(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public UserAccount getByUsername(String username) throws UserNotExistsException {
        var foundUser = this.findByUsername(username);
        if (foundUser.isEmpty()) {
            throw new UserNotExistsException("User does not exist");
        }
        return foundUser.get();
    }

    @Override
    public boolean delete(String username) {
        var deletedCount = userAccountRepository.deleteByUsername(username);
        return deletedCount > 0;
    }

    @Override
    public List<String> findUsernamesStartedWith(String usernameBeginPart, int pageNumberSinceOne)
            throws IllegalPageNumberException {
        checkPageNumber(pageNumberSinceOne);
        var pageRequest = PageRequest.of(pageNumberSinceOne - 1, serviceConstants.getPostsPerPage());
        return userAccountRepository.findByUsernameStartingWith(usernameBeginPart, pageRequest).stream()
                .map(UsernameOnlyUserAccountProjection::getUsername)
                .toList();
    }

    @Override
    public boolean isFrozen(String username) throws UserNotExistsException {
        var foundFrozen = userAccountRepository.findFrozenByUsername(username);
        System.err.println(username);
        if (foundFrozen.isEmpty()) {
            throw new UserNotExistsException("User not exists, so can not check is account frozen");
        }
        return foundFrozen.get().getFrozen();
    }

    @Override
    public void replaceRoleName(UserAccount receiver, RoleName roleName) {
        receiver.setRoleName(roleName);
        userAccountRepository.save(receiver);
    }

    @Override
    public void freeze(String suspectUsername, String suspenderUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException {
        var suspender = this.getByUsername(suspenderUsername);
        var suspect = this.getByUsername(suspectUsername);
        if (freezeVerifier.verify(suspender, suspect)) {
            suspect.setFrozen(true);
            this.save(suspect);
        } else {
            throw new FreezeStateSetNotPermittedException("Not permitted account freeze");
        }
    }

    @Override
    public void unfreeze(String suspectUsername, String suspenderUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException {
        var suspender = this.getByUsername(suspenderUsername);
        var suspect = this.getByUsername(suspectUsername);
        if (freezeVerifier.verify(suspender, suspect)) {
            suspect.setFrozen(false);
            this.save(suspect);
        } else {
            throw new FreezeStateSetNotPermittedException("Not permitted account unfreeze");
        }
    }

    @Override
    public void delete(String suspenderUsername, String suspectUsername)
            throws UserNotExistsException,
            FreezeStateSetNotPermittedException {
        var suspender = this.getByUsername(suspenderUsername);
        var suspect = this.getByUsername(suspectUsername);
        if (deleteVerifier.verify(suspender, suspect)) {
            userAccountRepository.delete(suspect);
        } else {
            throw new DeleteNotPermittedException("Not permitted account delete");
        }
    }

    @Override
    public void setRoleName(String roleStringName,
                        String receiverUsername,
                        String issuerUsername)
            throws UserNotExistsException,
            InvalidRoleNameException,
            RoleSetNotPermittedException {
        var roleToSet = RoleName.convertToRoleName(roleStringName);
        var receiver = this.getByUsername(receiverUsername);
        var issuer = this.getByUsername(issuerUsername);
        if (authorityComparator.compare(receiver, roleToSet) < 0) {
            var verifyInfo = new RoleIncreaseVerifier.VerifyInfo(receiver, roleToSet);
            if (roleIncreaseVerifier.verify(issuer, verifyInfo)) {
                this.replaceRoleName(receiver, roleToSet);
            } else {
                throw new RoleSetNotPermittedException("You can not increase the role for this account");
            }
        } else {
            var verifyInfo = new RoleDecreaseVerifier.VerifyInfo(receiver, roleToSet);
            if (roleDecreaseVerifier.verify(issuer, verifyInfo)) {
                this.replaceRoleName(receiver, roleToSet);
            } else {
                throw new RoleSetNotPermittedException("You can not decrease the role for this account");
            }
        }
    }
}
