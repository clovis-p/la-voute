package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

//TODO documenter l'api sur comment fonctionne le /register
@RestController
@CrossOrigin
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    private UserValidator userValidator;
    private UserDTOMapper userDTOMapper;
    private UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        userValidator = new UserValidator();
        userDTOMapper = new UserDTOMapper();
        this.userRepository = userRepository;
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

    //TODO retourner un statut SUCCESS a la place de l'id
    @PostMapping("/register")
    public int registerNewUser(@RequestBody UserDTO userDto) {
        logger.info("Registering new user" + userDto.getFirstName() + " " + userDto.getLastName());
        int id = -1;
        String errorMessage = userValidator.validateUser(userDto);

        if (errorMessage.isEmpty()) {
            User user = userDTOMapper.toUser(userDto);
            id = userRepository.save(user).getId();
            logger.info("The new user has been registered successfully with the id : " + id);
        } else {
            logger.error(errorMessage);
            throw new UserInvalidInformationsException(errorMessage);
        }
        return id;
    }


    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }
}
