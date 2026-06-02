package xyz.lavoute.web.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Handles all exception globally (or scoped if configured)
 *
 * tldr; turns exceptions into ResponseEntities
 * tldr; removes the need to use try catch everywhere
 *
 * @see <a href="https://medium.com/javarevisited/spring-boot-annotation-controlleradvice-global-exception-handler-a7d138c8d726">ControllerAdvice</a>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<Error> handleNoSuchAlgorithm(NoSuchAlgorithmException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("Attempt at generating signed url with SHA256 algorithm failed");
        LOGGER.error("Cryptographic function somehow was provided the wrong algorithm");

        Error customError = new Error("Signed Url generation failed");
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(customError);
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<Error> handleInvalidKey(InvalidKeyException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("Attempt at generating signed url with SHA256 algorithm failed");
        LOGGER.error("Cryptographic function was provided a key with defect configurations");

        Error customError = new Error("Signed Url generation failed");
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(customError);
    }

    @ExceptionHandler(SharingAttemptOnNonExistentFileException.class)
    public ResponseEntity<Error> handleSharingAttemptOnNonExistingFile(SharingAttemptOnNonExistentFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("Tried to share a file that doesn't exist");
        Error customError = new Error("Submitted file sharing request on a file that doesn't exist");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(customError);
    }

    @ExceptionHandler(DownloadNonExistingFileException.class)
    public ResponseEntity<Error> handleDownloadNonExistingFile(DownloadNonExistingFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("Tried to download a file that doesn't exist");
        Error customError = new Error("Tried to download a file that doesn't exist");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(customError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Error> handleUsernameNotFound(UsernameNotFoundException ex) {
        showOriginErrorMessage(ex);

        LOGGER.info("User with username: " + ex.getMessage() + " was not found");
        Error customError = new Error("User with username: " + ex.getMessage() + " was not found");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(customError);
    }

    @ExceptionHandler(NoOwnershipOnSharedFileException.class)
    public ResponseEntity<Error> handleNoOwnershipOnSharedFile(NoOwnershipOnSharedFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.info(ex.getMessage());
        Error customError = new Error("Connected User tried to access a file that he does not own");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(customError);
    }

    @ExceptionHandler(NoPermissionOnSharedFileException.class)
    public ResponseEntity<Error> handleNoPermissionOnSharedFile(NoPermissionOnSharedFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.info(ex.getMessage());
        Error customError = new Error("User tried to access a file but was denied, due to a lack of permissions");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(customError);
    }

    @ExceptionHandler(UnauthenticatedUserOnPrivateFileException.class)
    public ResponseEntity<Error> handleUnauthenticatedUserOnPrivateFile(UnauthenticatedUserOnPrivateFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("File was not public and the user was not connected");
        Error customError = new Error("Unauthenticated access attempt on a file that is not shared publicly");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(customError);
    }

    @ExceptionHandler(AccessOnNonExistingFileException.class)
    public ResponseEntity<Error> handleAccessOnNonExistingFileException(AccessOnNonExistingFileException ex) {
        showOriginErrorMessage(ex);

        LOGGER.error("Tried to get information on a file that doesn't exist");
        Error customError = new Error("Tried to get information on a file that doesn't exist");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(customError);
    }

    private void showOriginErrorMessage(Exception ex) {
        String origin = ex.getStackTrace()[0].getClassName();

        LOGGER.warn("Log message coming from {}", origin);
        LOGGER.warn(ex.getMessage());
    }
}
