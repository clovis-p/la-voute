package xyz.lavoute.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.RegistrationRequestDTO;
import xyz.lavoute.web.dto.UpdateProfileRequestDTO;
import xyz.lavoute.web.dto.UpdatedProfilePicDTO;
import xyz.lavoute.web.dto.UserResponseDTO;
import xyz.lavoute.web.exceptions.*;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.services.UserService;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        userService.registerUser(registrationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getSessionUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO response = userService.getUserProfileInformation(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateProfileRequestDTO updateProfileRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO response = userService.updateProfile(username, updateProfileRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-picture")
    public ResponseEntity<UpdatedProfilePicDTO> uploadProfilePicture(@RequestParam("picture") MultipartFile picture) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UpdatedProfilePicDTO returnDTO = userService.saveNewProfilePicture(username, picture);
        return ResponseEntity.ok(returnDTO);
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

    @ExceptionHandler(ProfilePictureErrorException.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    @ResponseBody
    Error handleProfilePictureErrorException(ProfilePictureErrorException exception) {
        return new Error(exception.getMessage());
    }
}
