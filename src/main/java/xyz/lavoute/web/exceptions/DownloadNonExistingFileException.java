package xyz.lavoute.web.exceptions;

public class DownloadNonExistingFileException extends RuntimeException {
  public DownloadNonExistingFileException(String message) {
    super(message);
  }
}
