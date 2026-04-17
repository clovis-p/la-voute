package xyz.lavoute.web.validation;

import org.springframework.stereotype.Component;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;

@Component
public class UserValidator {

    /**
     * Validating user's information according to certain criteria for each
     *
     * @param user the user to validate
     * @return an error message adapted to the errors // empty if no errors
     * @throws UserInvalidInformationsException throws and exception if the informations are null
     */
    public String validateUser(RegistrationRequestDTO user) {
        String returnMessage = "";

        if (user.getFirstName() == null || user.getLastName() == null || user.getUsername() == null || user.getPassword() == null) {
            throw new UserInvalidInformationsException("Certains champs sont null.");
        }

        if (user.getFirstName().length() < 3 || user.getFirstName().length() > 50) {
            returnMessage += "Le prénom doit être entre 3 et 50 caractères. \n";
        }

        if (user.getLastName().length() < 3 || user.getLastName().length() > 50) {
            returnMessage += "Le nom doit être entre 3 et 50 caractères. \n";
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            returnMessage += "Le nom d'utilisateur doit être entre 3 et 50 caractères. \n";
        }

        if (user.getPassword().length() < 8 || user.getPassword().length() > 100) {
            returnMessage += "Le mot de passe doit être entre 8 et 100 caractères. \n";
        }

        if (!user.getPassword().matches(".*[A-Z].*")) {
            returnMessage += "Le mot de passe doit avoir au moins une lettre majuscule. \n";
        }

        if (!user.getPassword().matches(".*[a-z].*")) {
            returnMessage += "Le mot de passe doit avoir au moins une lettre minuscule. \n";
        }

        if (!user.getPassword().matches(".*[0-9].*")) {
            returnMessage += "Le mot de passe doit avoir au moins un chiffre. \n";
        }

        if (!user.getPassword().matches(".*[^A-Za-z\\d].*")) {
            returnMessage += "Le mot de passe doit avoir au moins un symbole. \n";
        }

        return returnMessage;
    }
}
