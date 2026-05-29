package xyz.lavoute.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.*;
import xyz.lavoute.web.exceptions.ModificationNotAllowedException;
import xyz.lavoute.web.exceptions.ProfilePictureErrorException;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.validation.UserValidator;

import java.io.IOException;
import java.util.Base64;
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
        String encodedPicture;
        try {
            byte[] bytes = getClass().getResourceAsStream("/images/default-avatar.jpg").readAllBytes();
            encodedPicture = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new ProfilePictureErrorException("Erreur lors de la création de la création du profil");
        }

        User user = userDTOMapper.toUser(registrationRequestDTO, encodedPicture);
        User savedUser = userRepository.save(user);
        int id = savedUser.getId();

        logger.info("The new user has been registered successfully with the id : " + id);

        return savedUser;
    }

    public UserResponseDTO updateProfile(String username, UpdateProfileRequestDTO updateProfileRequestDTO) {
        logger.info("The user : " + username + " is modifying their profile.");
        User userEntity = getUserEntity(username);

        String errorMessage = userValidator.validateProfileUpdate(updateProfileRequestDTO, userEntity.getPassword());
        if (!errorMessage.isEmpty()) {
            throw new UserInvalidInformationsException(errorMessage);
        }

        if (updateProfileRequestDTO.getFirstName() != null && !updateProfileRequestDTO.getFirstName().isEmpty()) {
            userEntity.setFirstName(updateProfileRequestDTO.getFirstName());
        }
        if (updateProfileRequestDTO.getLastName() != null && !updateProfileRequestDTO.getLastName().isEmpty()) {
            userEntity.setLastName(updateProfileRequestDTO.getLastName());
        }
        if (updateProfileRequestDTO.getPassword() != null && !updateProfileRequestDTO.getPassword().isEmpty()) {
            userEntity.setPassword(encoder.encode(updateProfileRequestDTO.getPassword()));
        }
        userRepository.save(userEntity);

        return new UserResponseDTO(userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getProfilePic());
    }

    public UserResponseDTO getUserProfileInformation(String username) {
        User userEntity = getUserEntity(username);
        return new UserResponseDTO(userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getProfilePic());
    }

    public UpdatedProfilePicDTO saveNewProfilePicture(String username, MultipartFile picture) {
        User userEntity = getUserEntity(username);

        try {
            byte[] bytes = picture.getBytes();
            userEntity.setProfilePic(Base64.getEncoder().encodeToString(bytes));

        } catch (IOException e) {
            throw new ProfilePictureErrorException("Le photo de profil n'a pas pu être enregistrée.");
        }
        userRepository.save(userEntity);
        return new UpdatedProfilePicDTO(userEntity.getProfilePic());
    }

    /**
     * Getting the correct user authenticated with their username
     * @param username the username of the user currently authenticated
     * @return the user entity
     */
    private User getUserEntity(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("L'utilisateur n'existe pas.");
        }
        return user.get();
    }
}
