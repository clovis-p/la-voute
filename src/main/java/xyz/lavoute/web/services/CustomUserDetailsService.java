package xyz.lavoute.web.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.lavoute.web.models.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> retrievedUser = userService.getUserByUsername(username);

        if (retrievedUser.isEmpty()) {
            if (username.isBlank()) {
                username = "[empty_username]";
            }

            String msg = "User with the name: " + username + " does not match any user in the database";
            throw new UsernameNotFoundException(msg);
        }

        User user = retrievedUser.get();

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
