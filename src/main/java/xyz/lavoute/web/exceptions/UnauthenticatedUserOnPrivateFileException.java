package xyz.lavoute.web.exceptions;

public class UnauthenticatedUserOnPrivateFileException extends RuntimeException {
    public UnauthenticatedUserOnPrivateFileException(String message) {
        super(message);
    }
}
