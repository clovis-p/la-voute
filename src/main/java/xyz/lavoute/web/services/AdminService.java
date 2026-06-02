package xyz.lavoute.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.lavoute.web.dto.UserGetDTO;
import xyz.lavoute.web.exceptions.DeletionException;
import xyz.lavoute.web.exceptions.NotAnAdministratorException;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    public AdminService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    @Transactional
    public void deleteUser(Integer id, String username) {
        logger.info("The user " + username + " is trying to delete user with id " + id);
        Optional<User> userFound = userRepository.findUserById(id);
        if (userFound.isEmpty()) {
            throw new UserInvalidInformationsException("L'utilisateur à supprimer n'existe pas."); //TODO mettre l'exception du user non existant quand la branche master va être merge
        }
        User user = userFound.get();
        Optional<User> adminUser = userRepository.findUserByUsername(username);
        if (adminUser.isEmpty()) {
            throw new UserInvalidInformationsException("L'utilisateur qui tente de supprimer n'existe pas."); //TODO idem
        }
        if (!adminUser.get().getIsAdmin()) {
            logger.warn("The user " + username + " tried to delete another user but they are not an administrator.");
            throw new NotAnAdministratorException("Vous ne pouvez pas supprimer un autre utilisateur en étant pas admin.");
        }
        if (user.getUsername().equals(adminUser.get().getUsername())) {
            throw new DeletionException("Vous ne pouvez pas vous supprimer vous-même de l'application!");
        }
        fileRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

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

    private User getCurrentAdmin(String username) {
        Optional<User> adminUser = userRepository.findUserByUsername(username);
        if (adminUser.isEmpty()) {
            throw new UserInvalidInformationsException("L'utilisateur qui tente de supprimer n'existe pas."); //TODO idem
        }
        return adminUser.get();
    }

    private void verifyPermissions(User user) {
        if (!user.getIsAdmin()) {
            logger.warn("The user " + user.getUsername() + " tried to perform an admin command but they are not an administrator.");
            throw new NotAnAdministratorException("Vous ne pouvez pas supprimer un autre utilisateur en étant pas admin.");
        }
    }
}
