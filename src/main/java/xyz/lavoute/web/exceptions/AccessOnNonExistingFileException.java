package xyz.lavoute.web.exceptions;

public class AccessOnNonExistingFileException extends RuntimeException {
    public AccessOnNonExistingFileException(String message) {
        super(message);
    }
}
