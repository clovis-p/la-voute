package xyz.lavoute.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileVisibilityDTO {
    private String visibility;
    private List<String> usernames;

    public FileVisibilityDTO(String visibility, List<String> usernames) {
        this.visibility = visibility;
        this.usernames = usernames;
    }
}
