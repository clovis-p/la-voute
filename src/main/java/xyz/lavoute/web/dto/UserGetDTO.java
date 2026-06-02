package xyz.lavoute.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserGetDTO {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
}