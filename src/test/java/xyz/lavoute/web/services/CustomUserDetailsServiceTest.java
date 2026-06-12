package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class CustomUserDetailsServiceTest {
    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void givenEmptyString_whenGetting_ThenThrowsUsernameNotFoundException() {
        // Actor
        String emptyUsername = "";

        // Action and Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyUsername))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with the name: [empty_username] does not match any user in the database");
    }

    @Test
    void givenNonExistingUsername_whenGetting_ThenThrowsUsernameNotFoundException() {
        // Actor
        String nonExistingUsername = "I don't exist";

        // Action and Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nonExistingUsername))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with the name: " + nonExistingUsername + " does not match any user in the database");
    }

    @Test
    void givenExistingUsername_whenGetting_ThenReturnsUserDetails() {
        // Actor
        // For a test the encryted password is not needed or important
        User user = new User();
        String username = "I exist";
        user.setUsername(username);
        userRepository.save(user);

        UserDetails expected = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                // Ommit password assignment because it's not needed
                .build();

        // Action
        UserDetails actual = userDetailsService.loadUserByUsername(username);

        // Assert
        assertEquals(expected.getUsername(), actual.getUsername());
    }
}
