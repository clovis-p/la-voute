package xyz.lavoute.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateProfileRequestDTO {

    private String firstName;
    private String lastName;
    private String profilePicture;
    private String oldPassword;
    private String password;
}
