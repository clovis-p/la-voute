package xyz.lavoute.web.exceptions;

public class NoOwnershipOnSharedFileException extends RuntimeException {
    public NoOwnershipOnSharedFileException(int connectedUserId, int fileOwnerId, int fileId) {
        super("User with id: " + connectedUserId + " attempted to access file with id: " + fileId + " of owner with id: " + fileOwnerId);
    }
}
