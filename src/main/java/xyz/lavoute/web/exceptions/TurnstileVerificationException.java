package xyz.lavoute.web.exceptions;

public class TurnstileVerificationException extends RuntimeException {
    public TurnstileVerificationException(String message) {
        super(message);
    }
}
