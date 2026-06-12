package xyz.lavoute.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import xyz.lavoute.web.dto.UserGetDTO;
import xyz.lavoute.web.exceptions.*;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.models.Permission;
import xyz.lavoute.web.models.Share;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.PermissionRepository;
import xyz.lavoute.web.repositories.ShareRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final ShareRepository shareRepository;
    private final PermissionRepository permissionRepository;

    @Value("${storage.root}")
    private Path storageRoot;

    public AdminService(UserRepository userRepository, FileRepository fileRepository, ShareRepository shareRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.shareRepository = shareRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Deleting a specific user as an admin
     * @param id the id of the user to delete
     * @param username the username of the user currently authenticated
     */
    @Transactional
    public void deleteUser(Integer id, String username) {
        logger.info("The user " + username + " is trying to delete user with id " + id);
        //Finding the user to be deleted
        Optional<User> userFound = userRepository.findUserById(id);
        if (userFound.isEmpty()) {
            throw new UserNotFoundException("L'utilisateur à supprimer n'existe pas.");
        }
        User user = userFound.get();

        User adminUser = getCurrentAdmin(username);
        verifyPermissions(adminUser);

        //The admin cannot delete themselves
        if (user.getUsername().equals(adminUser.getUsername())) {
            throw new DeletionException("Vous ne pouvez pas vous supprimer vous-même de l'application!");
        }
        //Delete all the shares and permissions tied to the user and schedule deleting their files from the disk
        deleteUserData(user);
        //Delete all the files from the user in the BD
        fileRepository.deleteAllByUser(user);
        //Delete the user
        userRepository.delete(user);
    }

    /**
     * Called to get a list of all the users in the database
     * @param username the username of the admin authenticated
     * @return a collection of a DTO of the users
     */
    public Collection<UserGetDTO> getAllUsers(String username) {
        logger.info("the user " + username + "is trying to get all users information.");
        User adminAuthenticated = getCurrentAdmin(username);
        verifyPermissions(adminAuthenticated);

        Iterable<User> users = userRepository.findAll();
        Collection<UserGetDTO> response = new ArrayList<>();
        for (User currentUser : users) {
            response.add(new UserGetDTO(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getProfilePic()));
        }
        return response;
    }

    /**
     * Delete shares and permissions tied to the specified user and schedule deleting their files from the disk
     * @param user the user to delete
     */
    private void deleteUserData(User user) {
        Collection<File> userFiles = fileRepository.findAllByUser(user);
        Collection<Path> filePathsToDelete = new ArrayList<>();

        for (File currentFile : userFiles) {
            // Remove shares on the user's files and permissions referenced by these shares
            List<Permission> granteePermissions = shareRepository.findSharesByFileId(currentFile)
                    .stream()
                    .map(Share::getPermsId)
                    .filter(Objects::nonNull)
                    .toList();
            shareRepository.deleteAllByFileId(currentFile);
            permissionRepository.deleteAll(granteePermissions);

            filePathsToDelete.add(Paths.get(storageRoot.toString(), currentFile.getPath()).toAbsolutePath());
        }

        // Remove the shares granting this user access to other users' files before deleting the permissions they point to
        List<Permission> userPermissions = permissionRepository.findPermissionsByUser_Id(user.getId());
        if (!userPermissions.isEmpty()) {
            shareRepository.deleteAllByPermsIdIn(userPermissions);
        }
        permissionRepository.deleteAllByUser(user);

        scheduleDiskDeletion(filePathsToDelete);
    }

    /**
     * Remove the files from disk only once the database transaction has committed
     * @param filePaths the absolute paths of the files to delete
     */
    private void scheduleDiskDeletion(Collection<Path> filePaths) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    filePaths.forEach(AdminService.this::deleteFileFromDisk);
                }
            });
        } else {
            filePaths.forEach(this::deleteFileFromDisk);
        }
    }

    private void deleteFileFromDisk(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Could not delete file " + filePath, e);
        }
    }

    /**
     * Obtain the admin entity
     * @param username the username of the admin currently authenticated
     * @return the admin entity
     */
    private User getCurrentAdmin(String username) {
        Optional<User> adminUser = userRepository.findUserByUsername(username);
        if (adminUser.isEmpty()) {
            throw new UserNotFoundException("L'utilisateur qui tente de supprimer n'existe pas.");
        }
        return adminUser.get();
    }

    /**
     * Verify if the admin is actually an admin
     * @param user the admin entity
     */
    private void verifyPermissions(User user) {
        if (!user.getIsAdmin()) {
            logger.warn("The user " + user.getUsername() + " tried to perform an admin command but they are not an administrator.");
            throw new NotAnAdministratorException("Vous ne pouvez pas supprimer un autre utilisateur en étant pas admin.");
        }
    }
}
