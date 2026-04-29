package xyz.lavoute.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.services.FileService;

import java.util.Collection;

@RestController
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Uploading a new file in the database and the storage, ONLY FOR FILES, NOT DIRECTORIES
     * @param file the file the user is trying to upload
     * @param parentDirId the id of the parent directory, null if it's at the root
     * @return Status Accepted (202) when it worked
     */
    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadNewFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "parentDirId", required = false) Integer parentDirId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        fileService.uploadFile(file, username, parentDirId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * Creating a new directory in the database only, ONLY FOR DIRECTORIES, NOT FILES
     * @param directoryName the name of the directory who's being created
     * @param parentDirId the id of the parent directory, null if it's at the root
     * @return Status Accepted (202) when it worked
     */
    @PostMapping("/directory")
    public ResponseEntity<Integer> createNewDirectory(@RequestParam("directoryName") String directoryName, @RequestParam(value = "parentDirId", required = false) Integer parentDirId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        fileService.makeDirectory(directoryName, username, parentDirId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/obtain")
    public Collection<File> obtainFilesFromAGivenDirectory(@RequestParam(value = "parentDirId", required = false) Integer parentDirId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return fileService.obtainFilesFromSpecificDirectory(username, parentDirId);
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleStorageException(StorageException exception) {
        return new Error(exception.getMessage());
    }
}
