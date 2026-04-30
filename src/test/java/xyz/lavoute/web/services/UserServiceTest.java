package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
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

    User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = new User("username", "firstName", "lastName", "Password123!");
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

    /**
     * Registering tests
     */
    @Test
    void givenValidInformations_WhenRegistering_ThenUserIsSaved() {
        RegistrationRequestDTO userDTO = new RegistrationRequestDTO(user);

        userService.registerUser(userDTO);

        Optional<User> savedUser = userService.getUserByUsername(userDTO.getUsername());
        assertEquals(true, savedUser.isPresent());
    }

    @Test
    void givenValidInformations_WhenRegistering_ThenUsernameIsLowercase() {
        RegistrationRequestDTO userDTO = new RegistrationRequestDTO(user);
        userDTO.setUsername("USERNAME");

        userService.registerUser(userDTO);

        Optional<User> savedUser = userService.getUserByUsername("username");
        assertEquals(true, savedUser.isPresent());
    }

    @Test
    void givenInvalidInformations_WhenRegistering_ThenThrowsUserInvalidInformationsException() {
        RegistrationRequestDTO userDTO = new RegistrationRequestDTO(user);
        userDTO.setUsername("t");
        userDTO.setFirstName("e");
        userDTO.setLastName("s");
        userDTO.setPassword("t");

        assertThrows(UserInvalidInformationsException.class, () ->
                userService.registerUser(userDTO)
        );
        assertEquals(true, userRepository.findUserByUsername("t").isEmpty());
    }
}