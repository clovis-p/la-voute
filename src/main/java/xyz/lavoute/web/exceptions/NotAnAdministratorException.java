package xyz.lavoute.web.exceptions;

public class NotAnAdministratorException extends RuntimeException {
    public NotAnAdministratorException(String message) {
        super(message);
    }
}
