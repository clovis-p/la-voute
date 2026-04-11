package xyz.lavoute.web.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.lavoute.web.models.User;

@Getter
@Setter
public class UserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String password;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
    }

    public UserDTO() {
    }
}
