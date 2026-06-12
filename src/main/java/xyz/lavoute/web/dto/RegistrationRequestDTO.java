package xyz.lavoute.web.dto;

import lombok.*;
import xyz.lavoute.web.models.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistrationRequestDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String password;

    @ToString.Exclude
    private String cfTurnstileResponse;

    public RegistrationRequestDTO(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public RegistrationRequestDTO(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
    }
}
