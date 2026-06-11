package xyz.lavoute.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.*;
import xyz.lavoute.web.exceptions.*;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.services.TurnstileService;
import xyz.lavoute.web.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final TurnstileService turnstileService;

    public UserController(UserService userService, TurnstileService turnstileService) {
        this.userService = userService;
        this.turnstileService = turnstileService;
    }

    /**
     * Registering a new user in the database while validating their information and encrypting their password
     *
     * @param registrationRequestDto the user to save
     * @return the HTTPStatus "Created" (201) if the information were valid
     * @throws UserInvalidInformationsException with the error message when some information are not valid
     */
    @PostMapping("/register")
    public ResponseEntity<Integer> registerNewUser(@RequestBody RegistrationRequestDTO registrationRequestDto) {
        if (turnstileService.verifyCfToken(registrationRequestDto.getCfTurnstileResponse())) {
            throw new TurnstileVerificationException("Failed Captcha verification");
        }
        userService.registerUser(registrationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Called whenever the frontend need information from the user (username, first and last name, profile picture)
     * @return a dto containing the user information
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> getSessionUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        MeResponseDTO response = userService.getUserProfileInformation(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/obtain-picture")
    public ResponseEntity<PictureDTO> getUserPicture() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        PictureDTO response = userService.getUserPicture(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Called when the user is editing their profile
     * @param updateProfileRequestDTO a dto containing the needed information to edit or to verify (old password, first and last name, new password)
     * @return a dto containing the new information
     */
    @PutMapping("/edit")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateProfileRequestDTO updateProfileRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO response = userService.updateProfile(username, updateProfileRequestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Called when the user is trying to change their profile picture
     * @param picture the new picture
     * @return a dto containing only the encoded profile picture for live change on the frontend
     */
    @PatchMapping("/update-picture")
    public ResponseEntity<UpdatedProfilePicDTO> uploadProfilePicture(@RequestParam("picture") MultipartFile picture) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UpdatedProfilePicDTO returnDTO = userService.saveNewProfilePicture(username, picture);
        return ResponseEntity.ok(returnDTO);
    }

    /**
     * All the exceptions handling
     */
    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(TurnstileVerificationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    Error handleTurnstileVerification(TurnstileVerificationException exception) {
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

    @ExceptionHandler(ProfilePictureErrorException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ResponseBody
    Error handleProfilePictureErrorException(ProfilePictureErrorException exception) {
        return new Error(exception.getMessage());
    }
}
