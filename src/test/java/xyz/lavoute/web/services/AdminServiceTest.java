package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.lavoute.web.dto.UserGetDTO;
import xyz.lavoute.web.exceptions.DeletionException;
import xyz.lavoute.web.exceptions.NotAnAdministratorException;
import xyz.lavoute.web.exceptions.UserNotFoundException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.PermissionRepository;
import xyz.lavoute.web.repositories.ShareRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private ShareRepository shareRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private AdminService adminService;

    private User mockAdmin;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockAdmin = new User("adminUser", "Admin", "User", "Password1!", null);
        mockAdmin.setId(1);
        mockAdmin.setIsAdmin(true);

        mockUser = new User("regularUser", "Regular", "User", "Password1!", null);
        mockUser.setId(2);
        mockUser.setIsAdmin(false);
    }

    /**
     * Tests for deleting a user when it doesnt work
     */

    @Test
    void shouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findUserById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                adminService.deleteUser(99, "adminUser"));
    }

    @Test
    void shouldThrowUserNotFoundException_WhenAdminDoesNotExist() {
        when(userRepository.findUserById(99)).thenReturn(Optional.of(mockUser));
        when(userRepository.findUserByUsername("admin")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                adminService.deleteUser(99, "admin"));
    }

    @Test
    void shouldThrowDeletionException_whenAdminTryToDeleteThemselves() {
        when(userRepository.findUserById(1)).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findUserByUsername("adminUser")).thenReturn(Optional.of(mockAdmin));

        assertThrows(DeletionException.class, () ->
                adminService.deleteUser(1, "adminUser"));
    }

    @Test
    void shouldThrowNotAnAdministratorException_whenUserTryingToDeleteIsNotAnAdmin() {
        when(userRepository.findUserById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.findUserByUsername("admin")).thenReturn(Optional.of(mockUser));

        assertThrows(NotAnAdministratorException.class, () ->
                adminService.deleteUser(1, "admin"));
    }

    /**
     *  Tests for deleting a user when it works
     */
    @Test
    void shouldDeleteUser_whenItWorks(@TempDir Path tempDir) throws IOException {
        ReflectionTestUtils.setField(adminService, "storageRoot", tempDir);

        Path tempFile = tempDir.resolve("abc123");
        Files.writeString(tempFile, "content");

        File mockFile = new File();
        mockFile.setPath("abc123");
        mockFile.setIsDirectory(false);

        when(userRepository.findUserById(2)).thenReturn(Optional.of(mockUser));
        when(userRepository.findUserByUsername("adminUser")).thenReturn(Optional.of(mockAdmin));
        when(fileRepository.findAllByUser(mockUser)).thenReturn(List.of(mockFile));

        assertDoesNotThrow(() -> adminService.deleteUser(2, "adminUser"));

        assertFalse(Files.exists(tempFile));
        verify(fileRepository).deleteAllByUser(mockUser);
        verify(userRepository).delete(mockUser);
    }

    /**
     * Tests for getting all users
     */
    @Test
    void shouldReturnEmptyList_whenNoUsersInDatabase() {
        when(userRepository.findUserByUsername("adminUser")).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserGetDTO> result = adminService.getAllUsers("adminUser");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllUsers_WhenUsersAreExistingInDatabase() {
        when(userRepository.findUserByUsername("adminUser")).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findAll()).thenReturn(List.of(mockAdmin, mockUser));

        Collection<UserGetDTO> result = adminService.getAllUsers("adminUser");
        assertEquals(2, result.size());
    }

}
