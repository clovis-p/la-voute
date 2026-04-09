package xyz.lavoute.web.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private Boolean isAdmin;
    private String profilePic;

    public User(String firstName, String lastName, Boolean isAdmin, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.profilePic = profilePic;
    }
}
