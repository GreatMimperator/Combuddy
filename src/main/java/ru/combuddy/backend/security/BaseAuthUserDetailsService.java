package ru.combuddy.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.combuddy.backend.controllers.user.service.interfaces.UserAccountService;
import ru.combuddy.backend.security.BaseAuthUserDetails;

import java.text.MessageFormat;

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
                .roles(userAccount.getRoles())
                .build();
    }
}
