package xyz.lavoute.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
    this.userService = userService;
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
        userService.registerUser(registrationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }
}
