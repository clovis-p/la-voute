package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// NOTE assertEquals is used extensively more than assertTrue/False
// for better error messages
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void givenNoUsername_WhenGetting_ThenOptionalIsEmpty() {
        // Actor
        String emptyString = "";

        // Action
        Optional<User> optionalUser = userService.getUserByUsername(emptyString);

        // Assert
        assertEquals(true, optionalUser.isEmpty());
    }

    @Test
    void givenNonExistingUsername_WhenGetting_ThenOptionalIsEmpty() {
        // Actor
        String nonExistingUsername = "I don't exist";

        // Action
        Optional<User> optionalUser = userService.getUserByUsername(nonExistingUsername);

        // Assert
        assertEquals(true, optionalUser.isEmpty());
    }

    @Test
    void givenExistingUsername_WhenGetting_ThenOptionalHasUser() {
        // Actor
        // For a test the encryted password is not needed or important
        User user = new User();
        String username = "I exist";
        user.setUsername(username);

        userRepository.save(user);

        // Action
        Optional<User> optionalUser = userService.getUserByUsername(username);

        // Assert
        assertEquals(true, optionalUser.isPresent());
        assertEquals(username, optionalUser.get().getUsername());
    }
}