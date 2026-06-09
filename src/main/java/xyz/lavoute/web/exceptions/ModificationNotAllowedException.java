package xyz.lavoute.web.exceptions;

public class ModificationNotAllowedException extends RuntimeException {
  public ModificationNotAllowedException(String message) {
    super(message);
  }
}
