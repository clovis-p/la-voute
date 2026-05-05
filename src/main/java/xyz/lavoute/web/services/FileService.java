package xyz.lavoute.web.services;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class FileService {

    private final Path storageRoot = Path.of("storage");
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Value("${hashids.salt}")
    private String hashidsSalt;

    public FileService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    /**
     * Called when the user is trying to upload a file, is hashing the ID and put it in the "Path" column
     *
     * @param file        the file the user is trying to upload
     * @param username    the username of the user trying to upload
     * @param parentDirId the id of the parent IF NECESSARY (null if it's at the root)
     * @throws StorageException if the file is somehow empty or there's an error during the storing
     */
    public void uploadFile(MultipartFile file, String username, Integer parentDirId) {
        if (file.isEmpty()) {
            throw new StorageException("Impossible de stocker un fichier vide ou null.");
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new StorageException("Impossible d'obtenir le nom du fichier");
        }

        //Find the correct user who uploaded the file
        User userFound = getUserEntity(username);

        //Get the parent directory or null if it's at the root
        File parentFile = getParentDirectory(parentDirId, userFound);

        //Make the file entity
        File fileEntity = new File(storageRoot.toString(), Path.of(file.getOriginalFilename()).getFileName().toString(), false, true, userFound, parentFile);
        fileEntity.setFileSize(file.getSize());

        //Saving in the database to get the id
        fileEntity = fileRepository.save(fileEntity);

        //Hasing the id to get the hashed file name
        String hashedFileName = obtainHashedFileName(fileEntity.getId());
        fileEntity.setPath(hashedFileName);
        fileRepository.save(fileEntity);

        //Putting the file in the storage
        Path destinationFile = this.storageRoot.resolve(hashedFileName);
        try {
            Files.createDirectories(storageRoot); //Create the storage folder if it doesn't exist
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file.");
        }
        //Unlocking the file once the upload is completed
        fileEntity.setIsLocked(false);
        fileRepository.save(fileEntity);
    }

    /**
     * Called when the user is trying to create a directory
     *
     * @param name        the name of the directory
     * @param username    the username of the user trying to create a directory
     * @param parentDirId the id of the parent IF NECESSARY (null if it's at the root)
     */
    public void makeDirectory(String name, String username, Integer parentDirId) {
        //Find the correct user who made the directory
        User userFound = getUserEntity(username);

        //Get the parent directory or null if it's at the root
        File parentFile = getParentDirectory(parentDirId, userFound);
        File dirEntity = new File(storageRoot.toString(), name, true, true, userFound, parentFile);
        //Saving a first time to get the id
        fileRepository.save(dirEntity);

        //Obtaining the hashed name from the id
        String hashedFileName = obtainHashedFileName(dirEntity.getId());
        dirEntity.setPath(hashedFileName);
        dirEntity.setIsLocked(false);
        //Saving one last time when everything is done
        fileRepository.save(dirEntity);
    }

    /**
     * Get the files from a given directory
     * @param username username of the user currently authenticated
     * @param parentDirId the id of the parent folder // null if it's at the root
     * @return a Collection<FileGetDTO> with only the informations required
     */
    public Collection<FileGetDTO> obtainFilesFromSpecificDirectory(String username, Integer parentDirId) {
        User userFound = getUserEntity(username);

        File parentDir = getParentDirectory(parentDirId, userFound);
        Collection<File> files = fileRepository.findAllByParentDirAndUser(parentDir, userFound);

        Collection<FileGetDTO> filesDTO = new ArrayList<>();
        //Make all the files found into the DTO to not send back useless information
        for (File file : files) {
            filesDTO.add(new FileGetDTO(file));
        }

        return filesDTO;
    }

    public FileGetDTO renameFile(int fileId, String username, String newName) {
        User userFound = getUserEntity(username);

        File fileEntity = fileRepository.findFileById(fileId);
        if (fileEntity == null) {
            throw new StorageException("Le fichier n'existe pas.");
        }

        if (!fileEntity.getUser().equals(userFound)) {
            throw new StorageException("Le fichier n'appartient pas à l'utilisateur.");
        }
        fileEntity.setName(newName);
        fileRepository.save(fileEntity);

        return new FileGetDTO(fileEntity);
    }

    /**
     * Called when you need to hash the id of a file or directory
     *
     * @param id the id you need to hash
     * @return the hashed id that's going to be used for the name in the storage
     */
    private String obtainHashedFileName(int id) {
        //Renaming the file with the hash
        Hashids hashids = new Hashids(hashidsSalt, 8);
        return hashids.encode(id);
    }

    /**
     * Called when you want to find the parent of a file or directory / if it exists
     *
     * @param parentDirId the id of the parent directory, null if it's at the root
     * @return null if it's at the root, or the directory found
     * @throws StorageException if the id given doesn't exist or if it's not a directory
     */
    private File getParentDirectory(Integer parentDirId, User user) {
        //If the id is null, it means the file/directory is going to be at the root
        if (parentDirId == null) {
            return null;
        }

        File parentDirectory = fileRepository.findFileById(parentDirId);
        //If the id given is a directory that doesn't exist
        if (parentDirectory == null) {
            throw new StorageException("Le dossier parent n'existe pas.");
        }
        //If the parent directory is not associated to the user (normally shouldn't happen, but we never know)
        if (!parentDirectory.getUser().equals(user)) {
            throw new StorageException("Le parent n'est pas associé à l'utilisateur.");
        }
        //If the id given is a file and not a directory
        if (!parentDirectory.getIsDirectory()) {
            throw new StorageException("Le parent doit être un dossier.");
        }
        return parentDirectory;
    }

    private User getUserEntity(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new StorageException("L'utilisateur n'existe pas.");
        }
        return user.get();
    }
}