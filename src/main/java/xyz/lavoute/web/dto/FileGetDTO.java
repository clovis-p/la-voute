package xyz.lavoute.web.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.lavoute.web.models.File;

import java.time.LocalDate;

@Getter
@Setter
public class FileGetDTO {
    private int id;
    private String name;
    private Boolean isDirectory;
    private String username;
    private Integer parentDirId;
    private String parentDirName;
    private LocalDate date;
    private long size;

    public FileGetDTO(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.isDirectory = file.getIsDirectory();
        this.username = file.getUser().getUsername();
        this.parentDirId = file.getParentDir() != null ? file.getParentDir().getId() : null;
        this.parentDirName = file.getParentDir() != null ? file.getParentDir().getName() : null;
        this.date = file.getDate();
        this.size = file.getFileSize();
    }
}
