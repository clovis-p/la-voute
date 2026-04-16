package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.UserDTO;
import xyz.lavoute.web.dto.UserDTOMapper;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.validation.UserValidator;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserValidator userValidator;
    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public HomeController(UserRepository userRepository) {
        this.userDTOMapper = new UserDTOMapper();
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
        this.userValidator = new UserValidator();
    }

    private static final List<String> SUBTITLES = List.of(
            "Votre IRL Ender Chest",
            "Rangez. Retrouvez. Respirez.",
            "Le désordre, c'est fini.",
            "Tout à sa place, toujours.",
            "Votre mémoire physique."
    );

    private final Random random = new Random();

    @GetMapping("/api/home")
    public Map<String, String> home() {
        String subtitle = SUBTITLES.get(random.nextInt(SUBTITLES.size()));
        return Map.of("titleMessage", "Bienvenue dans La Voûte!", "subtitleMessage", subtitle);
    }

    /**
     * Registering a new user in the database while validating their information and encrypting their password
     *
     * @param userDto the user to save
     * @return the HTTPStatus "Created" (201) if the information were valid
     * @throws UserInvalidInformationsException with the error message when some informations are not valid
     */
    @PostMapping("/register")
    public ResponseEntity<Integer> registerNewUser(@RequestBody UserDTO userDto) {
        logger.info("Registering new user" + userDto.getFirstName() + " " + userDto.getLastName());
        int id = -1;
        String errorMessage = userValidator.validateUser(userDto);

        if (errorMessage.isEmpty()) {
            User user = userDTOMapper.toUser(userDto);
            user.setPassword(encoder.encode(user.getPassword()));
            id = userRepository.save(user).getId();
            logger.info("The new user has been registered successfully with the id : " + id);
        } else {
            logger.error(errorMessage);
            throw new UserInvalidInformationsException(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }
}
