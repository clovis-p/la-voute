package xyz.lavoute.web.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.FileDownloadDTO;
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.dto.PatchRequest;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.services.FileService;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@RestController
@CrossOrigin //TODO Add specific domain for production
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
    public Collection<FileGetDTO> obtainFilesFromAGivenDirectory(@RequestParam(value = "parentDirId", required = false) Integer parentDirId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return fileService.obtainFilesFromSpecificDirectory(username, parentDirId);
    }

    /**
     * Can be used for renaming and moving a file
     * @param id the id of the file to patch
     * @param request a record containing the newName or the newParentId
     * @return a FileGetDTO with the new information
     */
    @PatchMapping("/{id}")
    public FileGetDTO patchFile(@PathVariable int id, @RequestBody PatchRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return fileService.patchFile(id, request, username);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Integer> deleteFile(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        fileService.deleteFile(id, username);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
  
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        FileDownloadDTO downloadDTO = fileService.loadFileAsResource(username, id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDTO.getMimeType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(downloadDTO.getFileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(downloadDTO.getResource());
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleStorageException(StorageException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    Error handleUserNotFoundException(UserNotFoundException exception) {
        return new Error(exception.getMessage());
    }
}
