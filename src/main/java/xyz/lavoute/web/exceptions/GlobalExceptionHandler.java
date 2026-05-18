package xyz.lavoute.web.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    private void showOriginErrorMessage(Exception ex) {
        String origin = ex.getStackTrace()[0].getClassName();

        LOGGER.warn("Log message coming from {}", origin);
        LOGGER.warn(ex.getMessage());
    }
}
