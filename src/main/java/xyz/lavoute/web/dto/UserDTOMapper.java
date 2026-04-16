package xyz.lavoute.web.dto;

import org.springframework.stereotype.Component;
import xyz.lavoute.web.models.User;

@Component
public class UserDTOMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(user);
    }

    public User toUser(UserDTO userDTO) {
        return new User(userDTO.getUsername(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getPassword());
    }
}
