package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import xyz.lavoute.web.dto.MeResponseDTO;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UpdateProfileRequestDTO;
import xyz.lavoute.web.dto.UserResponseDTO;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// NOTE assertEquals is used extensively more than assertTrue/False
// for better error messages
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = new User("username", "firstName", "lastName", encoder.encode("Password123!"), "null");
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

    @Test
    void givenValidInformations_WhenRegistering_DefaultProfilePictureIsUsed() {
        RegistrationRequestDTO userDTO = new RegistrationRequestDTO(user);
        userDTO.setUsername("username");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDTO.setPassword("Soleil01!");
        User user = userService.registerUser(userDTO);

        assertTrue(user.getProfilePic().startsWith("/9j/"));
    }

    @Test
    void givenValidInformations_whenUpdatingProfile_ThenUserIsUpdated() {
        userRepository.save(user);
        userService.updateProfile(user.getUsername(), new UpdateProfileRequestDTO("firstNameModified", "lastNameModified", "Password123!", "Password456!"));

        Optional<User> updatedUser = userRepository.findUserByUsername("username");
        assertEquals("firstNameModified", updatedUser.get().getFirstName());
        assertEquals("lastNameModified", updatedUser.get().getLastName());
        assertTrue(encoder.matches("Password456!", updatedUser.get().getPassword()));
    }

    @Test
    void givenOnlyOneInformation_whenUpdatingProfile_ThenOnlyOneInformationIsUpdated() {
        userRepository.save(user);
        userService.updateProfile(user.getUsername(), new UpdateProfileRequestDTO("firstNameModified", "", "", ""));

        Optional<User> updatedUser = userRepository.findUserByUsername("username");
        assertEquals("firstNameModified", updatedUser.get().getFirstName());
        assertEquals("lastName", updatedUser.get().getLastName());
        assertTrue(encoder.matches("Password123!", updatedUser.get().getPassword()));
    }

    @Test
    void givenInvalidInformation_WhenUpdatingProfile_ThenThrowsUserInvalidInformationsException() {
        userRepository.save(user);
        assertThrows(UserInvalidInformationsException.class, () ->
                userService.updateProfile(user.getUsername(), new UpdateProfileRequestDTO("aa", "", "", "")));
    }

    @Test
    void givenNullInformation_WhenUpdatingProfile_ThenUpdateNothing() {
        userRepository.save(user);
        userService.updateProfile(user.getUsername(), new UpdateProfileRequestDTO(null, null, null, null));

        Optional<User> updatedUser = userRepository.findUserByUsername("username");
        assertEquals("firstName", updatedUser.get().getFirstName());
        assertEquals("lastName", updatedUser.get().getLastName());

    }

    @Test
    void givenValidPicture_whenSavingProfilePicture_ThenProfilePictureIsUpdated() {
        userRepository.save(user);
        MockMultipartFile picture = new MockMultipartFile("picture", "profile.jpg", "image/jpeg", "fakeImageBytes".getBytes());

        userService.saveNewProfilePicture(user.getUsername(), picture);

        Optional<User> updatedUser = userRepository.findUserByUsername("username");
        assertNotNull(updatedUser.get().getProfilePic());
        assertTrue(updatedUser.get().getProfilePic().startsWith("Zm"));
    }

    @Test
    void givenInexistingUser_whenDoingAnything_ThenReturnUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                userService.updateProfile(user.getUsername(), new UpdateProfileRequestDTO("firstNameModified", "", "", "")));
    }

    @Test
    void givenExistingUser_whenGettingProfileInformation_ThenProfileInformationIsReturned() {
        userRepository.save(user);

        MeResponseDTO result = userService.getUserProfileInformation(user.getUsername());

        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }
}