package xyz.lavoute.web.validation;

import org.springframework.stereotype.Component;
import xyz.lavoute.web.dto.UserDTO;

@Component
public class UserValidator {

    //TODO traduire les messages d'erreurs
    public String validateUser(UserDTO user) {
        String returnMessage = "";

        if (user.getFirstName() == null || user.getLastName() == null || user.getUsername() == null || user.getPassword() == null) {
            returnMessage += "Some fields are null. \n";
        }

        if (user.getFirstName().length() < 3 || user.getLastName().length() > 50) {
            returnMessage += "First name must be between 3 and 50 characters. \n";
        }

        if (user.getLastName().length() < 3 || user.getFirstName().length() > 50) {
            returnMessage += "Last name must be between 3 and 50 characters. \n";
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            returnMessage += "Username must be between 3 and 50 characters. \n";
        }

        if (user.getPassword().length() < 7) {
            returnMessage += "Password must be at least 7 characters. \n";
        }

        if (!user.getPassword().matches(".*[A-Z].*")) {
            returnMessage += "Password must contain at least one uppercase letter. \n";
        }

        if (!user.getPassword().matches(".*[a-z].*")) {
            returnMessage += "Password must contain at least one lowercase letter. \n";
        }

        if  (!user.getPassword().matches(".*[0-9].*")) {
            returnMessage += "Password must contain at least one digit. \n";
        }

        if (!user.getPassword().matches(".*[^A-Za-z\\d].*")) {
            returnMessage += "Password must contain at least one symbol. \n";
        }

        return returnMessage;
    }
}
