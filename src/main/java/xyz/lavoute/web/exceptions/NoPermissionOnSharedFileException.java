package xyz.lavoute.web.exceptions;

public class NoPermissionOnSharedFileException extends RuntimeException {
    public NoPermissionOnSharedFileException(int connectedUserId, int fileOwnerId, int fileId) {
        super("User with id: " + connectedUserId + " attempted to access file with id: " + fileId + " of owner with id: " + fileOwnerId + " but was denied, due to a lack of permissions");
    }
}
