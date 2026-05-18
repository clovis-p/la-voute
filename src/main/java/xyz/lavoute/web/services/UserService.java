package xyz.lavoute.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UserDTOMapper;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
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
        String errorMessage = userValidator.validateUser(registrationRequestDTO);
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
}
