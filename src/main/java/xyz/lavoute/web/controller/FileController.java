package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.services.FileService;

@RestController
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadNewFile(@RequestParam("file") MultipartFile file, @RequestParam User user) {
        fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
