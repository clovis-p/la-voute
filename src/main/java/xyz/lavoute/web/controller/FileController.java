package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.services.FileService;
import xyz.lavoute.web.services.UserService;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

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
    public Collection<FileGetDTO> obtainFilesFromAGivenDirectory(@RequestParam(value = "parentDirId", required = false) Integer parentDirId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return fileService.obtainFilesFromSpecificDirectory(username, parentDirId);
    }

    @PostMapping("share/public/{fileId}")
    public ResponseEntity<URI> shareFileExternal(
            Principal principal,
            UserService userService,
            @PathVariable int fileId
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String username = principal.getName();
        Optional<User> fileOwner = userService.getUserByUsername(username);
        Optional<File> file = fileService.getFileById(fileId);

        if (fileOwner.isEmpty()) {
            LOGGER.error("Could not find file owner with username: {}", username);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if (file.isEmpty()) {
            LOGGER.error("Could not find file with id: {}", fileId);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        URI signedShareUrl = fileService.generateSignedUrl(fileOwner.get(), file.get());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(signedShareUrl);
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleStorageException(StorageException exception) {
        return new Error(exception.getMessage());
    }
}
