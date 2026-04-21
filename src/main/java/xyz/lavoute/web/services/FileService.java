package xyz.lavoute.web.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileService {

    private final Path storageRoot = Path.of("storage");
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    public FileService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public void uploadFile(MultipartFile file, int userId, boolean isDirectory/*, File parentDir*/) {
        if (file.isEmpty()) {
            throw new StorageException("Impossible de stocker un fichier vide.");
        }

        User user = userRepository.findUserById(userId);
        //UTILISER HASHIDS (voir photo)

        File fileEntity = new File(storageRoot.toString(), file.getName(), isDirectory, true, user, null);

        if (!isDirectory) {
            Path destinationFile = this.storageRoot.resolve(Paths.get(file.getOriginalFilename()).normalize());

            try {
                InputStream inputStream = file.getInputStream();
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new StorageException("Failed to store file.");
            }
        }

        fileRepository.save(fileEntity);

    }
}
