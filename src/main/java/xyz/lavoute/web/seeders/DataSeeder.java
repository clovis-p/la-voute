package xyz.lavoute.web.seeders;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.services.FileService;
import xyz.lavoute.web.services.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);
    private final StandardServletMultipartResolver standardServletMultipartResolver;

    @Value("${hashids.salt}")
    private String hashidsSalt;

    private UserRepository userRepository;
    private UserService userService;

    private FileRepository fileRepository;
    private FileService fileService;

    private final Path storageRoot = Path.of("storage");

    @Autowired
    public DataSeeder(
            UserRepository userRepository,
            UserService userService,
            FileRepository fileRepository,
            FileService fileService,
            StandardServletMultipartResolver standardServletMultipartResolver) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.standardServletMultipartResolver = standardServletMultipartResolver;
    }

    private String obtainHashedFileName(int id) {
        //Renaming the file with the hash
        Hashids hashids = new Hashids(hashidsSalt, 8);
        return hashids.encode(id);
    }

    public void uploadFile(java.io.File file, User userFound, File parentFile) {
        //Make the file entity
        File fileEntity = new File(
                storageRoot.toString(),
                file.getName(),
                false,
                true,
                userFound,
                parentFile
        );

        fileEntity.setFileSize(file.length());

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
            InputStream inputStream = Files.newInputStream(file.toPath());
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file.");
        }
        //Unlocking the file once the upload is completed
        fileEntity.setIsLocked(false);
        fileRepository.save(fileEntity);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("User Seeder running ...");
        LOGGER.info("Flushing User, File tables and storage directory ...");
        fileRepository.deleteAll();
        userRepository.deleteAll();
        java.io.File storageDir = new java.io.File("storage/");
        storageDir.delete();

        LOGGER.info("Creating Admin ...");
        var adminRegisterRequest = new RegistrationRequestDTO(
                "matanteAdmin",
                "James",
                "Matthew",
                "Matante123!"
        );
        User adminUser = userService.registerUser(adminRegisterRequest);

        adminUser.setIsAdmin(true);
        User updatedAdmin = userRepository.save(adminUser);

        LOGGER.info("Creating first user ...");
        var firstUserRegisterRequest = new RegistrationRequestDTO(
                "usager1",
                "Patricia",
                "Robert",
                "Lavoute1!"
        );
        User firstUser = userService.registerUser(firstUserRegisterRequest);

        fileService.makeDirectory("firstUserEmptyDirAtRoot", firstUser.getUsername(), null);
        File firstUserDirWithContent = fileService.makeDirectory(
                "firstUserDirAtRoot",
                firstUser.getUsername(),
                null
        );

        java.io.File fileAtRootFirstUser = new java.io.File("seeders_files/image_file.png");
        uploadFile(fileAtRootFirstUser, firstUser, null);

        java.io.File fileInDirFirstUser = new java.io.File("seeders_files/text_file.txt");
        uploadFile(fileInDirFirstUser, firstUser, firstUserDirWithContent);


        var secondUserRegisterRequest = new RegistrationRequestDTO(
                "usager2",
                "Linda",
                "Lisa",
                "Lavoute2!"
        );
        User secondUser = userService.registerUser(secondUserRegisterRequest);
        LOGGER.info("Default User 2 created: " + secondUser.toString());

        fileService.makeDirectory("secondUserEmptyDirAtRoot", secondUser.getUsername(), null);
        File secondtUserDirWithContent = fileService.makeDirectory(
                "secondUserDirAtRoot",
                secondUser.getUsername(),
                null
        );

        java.io.File fileAtRootSecondUser = new java.io.File("seeders_files/pdf_file.pdf");
        uploadFile(fileAtRootSecondUser, secondUser, null);

        java.io.File fileInDirSecondUser = new java.io.File("seeders_files/image_file.png");
        uploadFile(fileInDirSecondUser, secondUser, secondtUserDirWithContent);
    }
}
