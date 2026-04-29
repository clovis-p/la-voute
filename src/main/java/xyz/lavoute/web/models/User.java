package xyz.lavoute.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String username;
    private String firstName;
    private String lastName;
    @JsonIgnore // To not send the password to the frontend when getting the files
    private String password;
    private Boolean isAdmin;
    private String profilePic;
    private boolean isBanned;

    public User(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = false;
        this.profilePic = "ressources/images/user.png";
        this.isBanned = false;
    }
}
