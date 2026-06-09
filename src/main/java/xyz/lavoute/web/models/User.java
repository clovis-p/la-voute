package xyz.lavoute.web.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Table(name = "users")
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean isAdmin;
    @Column(columnDefinition = "LONGTEXT")
    private String profilePic;
    private boolean isBanned;

    public User(String username, String firstName, String lastName, String password, String profilePic) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = false;
        this.profilePic = profilePic;
        this.isBanned = false;
    }
}
