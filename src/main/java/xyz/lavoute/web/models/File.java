package xyz.lavoute.web.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String path;
    private String name;

    @Column(name = "is_directory")
    private Boolean isDirectory;

    @Column(name = "is_locked")
    private Boolean isLocked;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_dir_id")
    private File parentDir;

/*    @ManyToOne
    @JoinColumn(name = "type_id")
    private FileType type; */

    public File(String path, String name, Boolean isDirectory, Boolean isLocked, User user, File parentDir) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        this.isLocked = isLocked;
        this.user = user;
        this.parentDir = parentDir;
    }
}
