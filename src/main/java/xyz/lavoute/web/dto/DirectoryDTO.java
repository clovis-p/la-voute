package xyz.lavoute.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectoryDTO {
    private String directoryName;
    private Integer parentDirId;

    public DirectoryDTO(String directoryName, Integer parentDirId) {
        this.directoryName = directoryName;
        this.parentDirId = parentDirId;
    }

    public DirectoryDTO() {
    }
}
