package xyz.lavoute.web.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    private final Path storageRoot = Path.of("storage");

    public void uploadFile(MultipartFile file/*, User user, boolean isDirectory, File parentDir*/) {
        if (file.isEmpty()) {
            throw new StorageException("Impossible de stocker un fichier vide.");
        }
        //UTILISER HASHIDS (voir photo)
        //File file1 = new File(storageRoot, file.getName(), isDirectory, true, user, parentDir);

        Path destinationFile = this.storageRoot.resolve(Paths.get(file.getOriginalFilename()).normalize());

        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file.");
        }

    }
}
