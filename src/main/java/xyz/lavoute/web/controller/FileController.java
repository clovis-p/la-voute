package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.FileDownloadDTO;
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.dto.FileVisibilityDTO;
import xyz.lavoute.web.dto.PatchRequest;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.Permission;
import xyz.lavoute.web.models.Share;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.services.FileService;
import xyz.lavoute.web.services.PermissionService;
import xyz.lavoute.web.services.ShareService;
import xyz.lavoute.web.services.UserService;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final ShareService shareService;
    private final UserService userService;
    private final PermissionService permissionService;

    public FileController(
            FileService fileService,
            ShareService shareService,
            UserService userService,
            PermissionService permissionService
    ) {
        this.fileService = fileService;
        this.shareService = shareService;
        this.userService = userService;
        this.permissionService = permissionService;
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

    @GetMapping("share/{fileId}/download")
    public ResponseEntity<Resource> fileSharingDownload(Principal principal, @PathVariable int fileId) throws FileNotFoundException {
        Optional<File> maybeFile = fileService.getFileById(fileId);

        if (maybeFile.isEmpty()) {
            throw new FileNotFoundException();
        }

        Integer downloadedFileId = maybeFile.get().getId();

        if (fileIsPublic(maybeFile.get())) {
            LOGGER.info(
                    "Downloaded file with id: " + downloadedFileId + " because file is public"
            );

            User user = maybeFile.get().getUser();

            return getDownloadedFile(
                    user.getUsername(),
                    downloadedFileId
            );
        } else {
            if (principal == null) {
                LOGGER.error("File was not public and the user was not connected");
            }

            String username = principal.getName();
            Optional<User> maybeUser = userService.getUserByUsername(username);

            if (maybeUser.isEmpty()) {
                throw new UsernameNotFoundException(username);
            }

            if (userHasPermissionOnFile(maybeUser.get(), maybeFile.get())) {
                LOGGER.info(
                        "User with name: " + username + " downloaded file with id: " + downloadedFileId + " because user has permissions on file"
                );

                return getDownloadedFile(
                        username,
                        downloadedFileId
                );
            }
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    private ResponseEntity<Resource> getDownloadedFile(String username, Integer fileId) {
        FileDownloadDTO downloadDTO = fileService
                .loadFileAsResource(
                        username,
                        fileId
                );

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

    // IMPORTANT Si le endpoint marche pas c'est probablement que Spring Boot n'autorise pas les requests autre que GET avec un ancien csrf token.
    // Donc quand on hit "/login" il ne faut pas oublier de redemander le nouveau csrf avec "/api/csrf"
    @PostMapping("share/{fileId}/create")
    public ResponseEntity<Void> fileSharing(
            @PathVariable int fileId,
            @RequestBody(required = false) List<String> usernames
    ) throws FileNotFoundException {
        Optional<File> file = fileService.getFileById(fileId);

        if (file.isEmpty()) {
            throw new FileNotFoundException();
        }

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String connectedUsername = auth.getName();
        LOGGER.info("Connected user named: " + connectedUsername + " is attempting to share file with id: " + fileId);

        shareService.deleteSharesByFile(file.get());

        if (usernames == null) {
            Share share = new Share(null, file.get());
            shareService.saveShare(share);

            LOGGER.info("Created public permission and share for file with id: " + fileId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        }

        // If it has usernames
        usernames.forEach((username) -> {
            Optional<User> permittedUser = userService.getUserByUsername(username);

            if (permittedUser.isEmpty()) {
                throw new UsernameNotFoundException(username);
            } else {
                User user = permittedUser.get();

                Permission permission = new Permission("read", user);
                permissionService.savePermission(permission);

                Share share = new Share(permission, file.get());
                shareService.saveShare(share);

                LOGGER.info("Created permission and share for file with id: " + fileId + " to user with id: " + user.getId());
            }
        });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("{fileId}/visibility")
    public ResponseEntity<FileVisibilityDTO> getFileVisibility(@PathVariable int fileId) throws FileNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<File> file = fileService.getFileById(fileId);
        Optional<User> user = userService.getUserByUsername(username);

        if (file.isEmpty()) {
            throw new FileNotFoundException();
        }
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        if (!userIsFileOwner(user.get(), file.get())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Share> shares = shareService.findSharesByFile(file.get());

        boolean isPublic = false;
        for (Share share : shares) {
            if (share.getPermsId() == null) {
                isPublic = true;
                break;
            }
        }

        if (isPublic) {
            return ResponseEntity.ok(new FileVisibilityDTO("public", null));
        }

        List<String> usernames = shares.stream()
                .map(share -> share.getPermsId().getUser().getUsername())
                .toList();

        return ResponseEntity.ok(new FileVisibilityDTO(
                "private",
                usernames.isEmpty() ? null : usernames
        ));
    }

    @GetMapping("{fileId}")
    public ResponseEntity<FileGetDTO> getSharedFile(
            @PathVariable int fileId,
            Principal principal
    ) throws FileNotFoundException {
        Optional<File> maybeFile = fileService.getFileById(fileId);

        if (maybeFile.isEmpty()) throw new FileNotFoundException();

        ResponseEntity<FileGetDTO> response = ResponseEntity
                .status(HttpStatus.OK)
                .body(new FileGetDTO(maybeFile.get()));

        if (fileIsPublic(maybeFile.get())) {
            LOGGER.info("Returned public file dto with id: " + fileId);
            return response;
        }

        Optional<User> maybeUser = userService.getUserByUsername(principal.getName());
        if (maybeUser.isEmpty()) throw new UsernameNotFoundException(principal.getName());
        LOGGER.info("Authenticated user with id: " + maybeUser.get().getId() + " is attempting to request a file with id: " + fileId);

        if (userIsFileOwner(maybeUser.get(), maybeFile.get())) {
            LOGGER.info("Returned file dto with id: " + fileId + " to owner with id:" + maybeUser.get().getId());
            return response;
        }
        if (userHasPermissionOnFile(maybeUser.get(), maybeFile.get())) {
            LOGGER.info("Returned file dto with id: " + fileId + " to connected user with id: " + maybeUser.get().getId() + " that has permission");
            return response;
        }

        LOGGER.error("User with id: " + maybeUser.get().getId() + " attempted to access a file but got denied");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    private boolean userIsFileOwner(User connectedUser, File file) {
        return connectedUser.getId() == file.getUser().getId();
    }

    private boolean userHasPermissionOnFile(User connectedUser, File file) {
        List<Share> fileShares = shareService.findSharesByFile(file);

        List<Permission> filePermissions = fileShares
                .stream()
                .map(Share::getPermsId)
                .toList();

        for (Permission filePermission : filePermissions) {
            if (filePermission == null) return false;

            User user = filePermission.getUser();
            if (connectedUser.getId() == user.getId()) return true;
        }
        return false;
    }

    private boolean fileIsPublic(File file) {
        List<Share> fileShares = shareService.findSharesByFile(file);
        for (Share fileShare : fileShares) {
            Permission permission = fileShare.getPermsId();
            if (permission == null) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleStorageException(StorageException exception) {
        return new Error(exception.getMessage());
    }
}
