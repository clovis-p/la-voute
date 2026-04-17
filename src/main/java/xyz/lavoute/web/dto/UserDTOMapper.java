package xyz.lavoute.web.dto;

import org.springframework.stereotype.Component;
import xyz.lavoute.web.models.User;

@Component
public class UserDTOMapper {
    public RegistrationRequestDTO toDTO(User user) {
        return new RegistrationRequestDTO(user);
    }

    public User toUser(RegistrationRequestDTO registrationRequestDTO) {
        return new User(registrationRequestDTO.getUsername(), registrationRequestDTO.getFirstName(), registrationRequestDTO.getLastName(), registrationRequestDTO.getPassword());
    }
}
