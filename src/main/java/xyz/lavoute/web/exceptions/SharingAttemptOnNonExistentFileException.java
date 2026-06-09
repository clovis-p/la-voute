package xyz.lavoute.web.exceptions;

public class SharingAttemptOnNonExistentFileException extends RuntimeException {
    public SharingAttemptOnNonExistentFileException(String message) {
        super(message);
    }
}
