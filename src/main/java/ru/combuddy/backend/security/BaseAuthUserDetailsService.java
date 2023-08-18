package ru.combuddy.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;

import java.text.MessageFormat;

import static ru.combuddy.backend.entities.user.UserAccount.getRoles;

@Component
@AllArgsConstructor
public class BaseAuthUserDetailsService implements UserDetailsService {

    private UserAccountService userAccountService;

    @Override
    public BaseAuthUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var foundUserAccount = userAccountService.findByUsername(username);
        if (foundUserAccount.isEmpty()) {
            throw new UsernameNotFoundException(
                    new MessageFormat("User with username {0} not found").format(username));
        }
        var userAccount = foundUserAccount.get();
        return BaseAuthUserDetails.builder()
                .username(username)
                .password(userAccount.getBaseAuth().getEncryptedPassword())
                .isLocked(userAccount.getFrozen())
                .roles(getRoles(userAccount))
                .build();
    }
}
