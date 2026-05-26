package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UpdateProfileRequestDTO;
import xyz.lavoute.web.dto.UserResponseDTO;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.exceptions.ModificationNotAllowedException;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.repositories.UserRepository;
import xyz.lavoute.web.services.UserService;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
        this.userRepository = userRepository;
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

    @GetMapping("/me")
    public Map<String, String> getSessionUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return Map.of("username", username);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO response = userService.getUserProfileInformation(username, id);
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }


    @PutMapping("/{id}/edit")
    public ResponseEntity<UserResponseDTO> updateProfile(@PathVariable Integer id, @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO response = userService.updateProfile(username, id, updateProfileRequestDTO);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(ModificationNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    Error handleModificationNotAllowedException(ModificationNotAllowedException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    Error handleUserNotFoundException(UserNotFoundException exception) {
        return new Error(exception.getMessage());
    }
}
