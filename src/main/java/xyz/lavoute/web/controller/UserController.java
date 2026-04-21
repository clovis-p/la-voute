package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UserDTOMapper;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.validation.UserValidator;

@RestController
@CrossOrigin
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserValidator userValidator;
    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserController(UserValidator userValidator, UserDTOMapper userDTOMapper, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userDTOMapper = userDTOMapper;
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * Registering a new user in the database while validating their information and encrypting their password
     *
     * @param registrationRequestDto the user to save
     * @return the HTTPStatus "Created" (201) if the information were valid
     * @throws UserInvalidInformationsException with the error message when some informations are not valid
     */
    @PostMapping("/register")
    public ResponseEntity<Integer> registerNewUser(@RequestBody RegistrationRequestDTO registrationRequestDto) {
        logger.info("Registering new user" + registrationRequestDto.getFirstName() + " " + registrationRequestDto.getLastName());
        String errorMessage = userValidator.validateUser(registrationRequestDto);

        if (errorMessage.isEmpty()) {
            registrationRequestDto.setPassword(encoder.encode(registrationRequestDto.getPassword()));
            User user = userDTOMapper.toUser(registrationRequestDto);
            int id = userRepository.save(user).getId();
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
    xyz.lavoute.web.exceptions.Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }
}
