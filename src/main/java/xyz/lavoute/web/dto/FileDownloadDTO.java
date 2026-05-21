package xyz.lavoute.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
public class FileDownloadDTO {
    private Resource resource;
    private String fileName;
    private String mimeType;

    public FileDownloadDTO(Resource resource, String fileName, String mimeType) {
        this.resource = resource;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }
}
