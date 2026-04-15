package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.lavoute.web.dto.UserDTO;
import xyz.lavoute.web.validation.UserValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    private UserValidator userService;
    UserDTO user;

    @BeforeEach
    public void setUp() {
        userService = new UserValidator();
        user = new UserDTO();
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("Password1!");
    }

    @Test
    void shouldReturnEmptyString_whenUserIsValid() {
        String result = userService.validateUser(user);

        assertEquals("", result);
    }
    @Test
    void shouldReturnAdaptedError_whenUsernameDontHaveEnoughCharacters() {
        user.setUsername("us");
        String result = userService.validateUser(user);
        assertEquals("Le nom d'utilisateur doit être entre 3 et 50 caractères. \n", result);
    }
}
