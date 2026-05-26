package xyz.lavoute.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
}
