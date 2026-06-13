package xyz.lavoute.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isAdmin;
}
