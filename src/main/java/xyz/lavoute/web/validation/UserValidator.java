package xyz.lavoute.web.validation;

import org.springframework.stereotype.Component;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UpdateProfileRequestDTO;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;

import java.util.Optional;

@Component
public class UserValidator {

    private final UserRepository password;

    public UserValidator(UserRepository userRepository) {
        this.password = userRepository;
    }

    /**
     * Validation when a user is registering
     * @param user the dto of the user information
     * @return the appropriate error message
     */
    public String validateRegistration(RegistrationRequestDTO user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getUsername() == null || user.getPassword() == null) {
            throw new UserInvalidInformationsException("Certains champs sont null.");
        }

        String returnMessage = "";
        returnMessage += validateFirstName(user.getFirstName());
        returnMessage += validateLastName(user.getLastName());
        returnMessage += validateUsername(user.getUsername());
        returnMessage += validatePassword(user.getPassword());
        return returnMessage;
    }

    /**
     * Validation when a user is modifying their profile
     * @param user the userDTO with the modified informations
     * @return an appropriate error message
     */
    public String validateProfileUpdate(UpdateProfileRequestDTO user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getPassword() == null) {
            throw new UserInvalidInformationsException("Certains champs sont null.");
        }

        String returnMessage = "";
        returnMessage += validateFirstName(user.getFirstName());
        returnMessage += validateLastName(user.getLastName());
        returnMessage += validateUsername(user.getPassword());
        return returnMessage;
    }

    private String validateFirstName(String firstName) {
        String message = "";
        if (firstName.length() < 3 || firstName.length() > 50) {
            message += "Le prénom doit être entre 3 et 50 caractères. \n";
        }
        return message;
    }

    private String validateLastName(String lastName) {
        String message = "";
        if (lastName.length() < 3 || lastName.length() > 50) {
            message += "Le nom doit être entre 3 et 50 caractères. \n";
        }
        return message;
    }

    private String validateUsername(String username) {
        String message = "";
        if (username.length() < 3 || username.length() > 50) {
            message += "Le nom d'utilisateur doit être entre 3 et 50 caractères. \n";
        }
        Optional<User> userFound = password.findUserByUsername(username);
        if (userFound.isPresent()) {
            message += "Un utilisateur existe déjà avec ce username. \n";
        }
        return message;
    }

    private String validatePassword(String password) {
        String message = "";
        if (password.length() < 8 || password.length() > 100) {
            message += "Le mot de passe doit être entre 8 et 100 caractères. \n";
        }

        if (!password.matches(".*[A-Z].*")) {
            message += "Le mot de passe doit avoir au moins une lettre majuscule. \n";
        }

        if (!password.matches(".*[a-z].*")) {
            message += "Le mot de passe doit avoir au moins une lettre minuscule. \n";
        }

        if (!password.matches(".*[0-9].*")) {
            message += "Le mot de passe doit avoir au moins un chiffre. \n";
        }

        if (!password.matches(".*[^A-Za-z\\d].*")) {
            message += "Le mot de passe doit avoir au moins un symbole. \n";
        }
        return message;
    }
}
