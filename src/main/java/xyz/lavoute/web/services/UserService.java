package xyz.lavoute.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UpdateProfileRequestDTO;
import xyz.lavoute.web.dto.UserDTOMapper;
import xyz.lavoute.web.dto.UserResponseDTO;
import xyz.lavoute.web.exceptions.ModificationNotAllowedException;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.validation.UserValidator;

import java.util.Optional;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final UserValidator userValidator;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, UserDTOMapper userDTOMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.userValidator = userValidator;
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * The function returns an optional wrapper and
     * lets the programmer handle the "null" case.
     *
     * @param username
     * @return Optional User
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Registering a new user in the database if the informations are valid
     * @param registrationRequestDTO the user model to validate and put in the database
     * @throws UserInvalidInformationsException when some informations are invalid
     */
    public User registerUser(RegistrationRequestDTO registrationRequestDTO) {
        logger.info("Registering new user : " + registrationRequestDTO.getFirstName() + " " + registrationRequestDTO.getLastName());

        registrationRequestDTO.setUsername(registrationRequestDTO.getUsername().toLowerCase());
        String errorMessage = userValidator.validateRegistration(registrationRequestDTO);
        if (!errorMessage.isEmpty()) {
            logger.error(errorMessage + registrationRequestDTO.getUsername());
            throw new UserInvalidInformationsException(errorMessage);
        }
        registrationRequestDTO.setPassword(encoder.encode(registrationRequestDTO.getPassword()));
        User user = userDTOMapper.toUser(registrationRequestDTO);
        User savedUser = userRepository.save(user);
        int id = savedUser.getId();

        logger.info("The new user has been registered successfully with the id : " + id);

        return savedUser;
    }

    public UserResponseDTO updateProfile(String username, Integer id, UpdateProfileRequestDTO updateProfileRequestDTO) {
        logger.info("The user : " + username + " is modifying their profile.");

        User userEntity = getUserEntity(id);
        checkOwnership(username, userEntity);

        String errorMessage = userValidator.validateProfileUpdate(updateProfileRequestDTO);
        if (!errorMessage.isEmpty()) {
            throw new UserInvalidInformationsException(errorMessage);
        }

        userEntity.setFirstName(updateProfileRequestDTO.getFirstName());
        userEntity.setLastName(updateProfileRequestDTO.getLastName());
        userEntity.setUsername(username);
        userEntity.setPassword(encoder.encode(updateProfileRequestDTO.getPassword()));
        userRepository.save(userEntity);

        return new UserResponseDTO(userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getProfilePic());
    }

    public UserResponseDTO getUserProfileInformation(String username, Integer id) {
        logger.info("The user : " + username + " is consulting their profile.");

        User userEntity = getUserEntity(id);
        checkOwnership(username, userEntity);

        return new UserResponseDTO(userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getProfilePic());
    }

    private void checkOwnership(String username, User user) {
        if (!user.getUsername().equals(username)) {
            logger.warn(username + " tried to modify someone else's profile with the username : " + user.getUsername());
            throw new ModificationNotAllowedException("Vous ne pouvez pas modifer ce profil");
        }
    }

    /**
     * Getting the correct user authenticated with their username
     * @param id the id of the user authenticated
     * @return the user entity
     */
    private User getUserEntity(Integer id) {
        Optional<User> user = userRepository.findUserById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("L'utilisateur n'existe pas.");
        }
        return user.get();
    }
}
