package xyz.lavoute.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    RegistrationRequestDTO user;

    @BeforeEach
    public void setUp() {
        user = new RegistrationRequestDTO();
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("Password1!");
    }

    /**
     * Test for a valid user
     */
    @Test
    void shouldReturnEmptyString_whenUserIsValid() {
        String result = userValidator.validateUser(user);
        assertEquals("", result);
    }

    /**
     * Tests for null informations
     */
    @Test
    void shouldThrowUserInvalidInformationsException_whenUsernameIsNull() {
        user.setUsername(null);
        assertThrows(UserInvalidInformationsException.class, () -> userValidator.validateUser(user));
    }

    @Test
    void shouldThrowUserInvalidInformationsException_whenFirstNameIsNull() {
        user.setFirstName(null);
        assertThrows(UserInvalidInformationsException.class, () -> userValidator.validateUser(user));
    }

    @Test
    void shouldThrowUserInvalidInformationsException_whenLastNameIsNull() {
        user.setLastName(null);
        assertThrows(UserInvalidInformationsException.class, () -> userValidator.validateUser(user));
    }

    @Test
    void shouldThrowUserInvalidInformationsException_whenPasswordIsNull() {
        user.setPassword(null);
        assertThrows(UserInvalidInformationsException.class, () -> userValidator.validateUser(user));
    }

    /**
     * Tests for the username validation
     */
    @Test
    void shouldReturnAdaptedError_whenUsernameDontHaveEnoughCharacters() {
        user.setUsername("us");
        String result = userValidator.validateUser(user);
        assertEquals("Le nom d'utilisateur doit être entre 3 et 50 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenUsernameHaveTooMuchCharacters() {
        user.setUsername("TheUsernameIsLongerThan50CharactersAndItWillReturnAnErrorMessage");
        String result = userValidator.validateUser(user);
        assertEquals("Le nom d'utilisateur doit être entre 3 et 50 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenUsernameIsAlreadyTaken() {
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(new User("username", "Test", "Test", "Password123!")));
        String result = userValidator.validateUser(user);
        assertEquals("Un utilisateur existe déjà avec ce username. \n", result);

    }

    /**
     * Tests for the first name validation
     */
    @Test
    void shouldReturnAdaptedError_whenFirstNameDontHaveEnoughCharacters() {
        user.setFirstName("us");
        String result = userValidator.validateUser(user);
        assertEquals("Le prénom doit être entre 3 et 50 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenFirstNameHaveTooMuchCharacters() {
        user.setFirstName("TheFirstNameIsLongerThan50CharactersAndItWillReturnAnErrorMessage");
        String result = userValidator.validateUser(user);
        assertEquals("Le prénom doit être entre 3 et 50 caractères. \n", result);
    }

    /**
     * Tests for the last name validation
     */
    @Test
    void shouldReturnAdaptedError_whenLastNameDontHaveEnoughCharacters() {
        user.setLastName("us");
        String result = userValidator.validateUser(user);
        assertEquals("Le nom doit être entre 3 et 50 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenLastNameHaveTooMuchCharacters() {
        user.setLastName("TheLastNameIsLongerThan50CharactersAndItWillReturnAnErrorMessage");
        String result = userValidator.validateUser(user);
        assertEquals("Le nom doit être entre 3 et 50 caractères. \n", result);
    }

    /**
     * Tests for the password validation
     */
    @Test
    void shouldReturnAdaptedError_whenPasswordDontHaveEnoughCharacters() {
        user.setPassword("Pass1!");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit être entre 8 et 100 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenPasswordHaveTooMuchCharacters() {
        user.setPassword("1!PasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPasswordPassword");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit être entre 8 et 100 caractères. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenPasswordIsMissingAnUppercaseCharacter() {
        user.setPassword("password1!");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit avoir au moins une lettre majuscule. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenPasswordIsMissingALowercaseCharacter() {
        user.setPassword("PASSWORD1!");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit avoir au moins une lettre minuscule. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenPasswordIsMissingANumberCharacter() {
        user.setPassword("Password!");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit avoir au moins un chiffre. \n", result);
    }

    @Test
    void shouldReturnAdaptedError_whenPasswordIsMissingASymbolCharacter() {
        user.setPassword("Password1");
        String result = userValidator.validateUser(user);
        assertEquals("Le mot de passe doit avoir au moins un symbole. \n", result);
    }
}
