package xyz.lavoute.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private User mockUser;
    private File mockParentDir;
    private File mockFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "hashidsSalt", "ASDKFJ87276dhfuFDH27");

        mockUser = new User("TestUser", "Test", "User", "Password1!");
        mockParentDir = new File("storage", "testFolder", true, false, mockUser, null);
        mockParentDir.setId(1);
        mockFile = new File("storage", "testFolder", false, false, mockUser, null);
    }

    /**
     * Tests for all the possible exceptions
     */
    @Test
    void shouldThrowStorageException_whenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(emptyFile, "testuser", mockUser.getId()));
    }

    @Test
    void shouldThrowStorageException_whenUserDoesNotExistForFiles() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "empty".getBytes());

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(file, "testUser", mockParentDir.getId()));
    }

    @Test
    void shouldThrowStorageException_whenParentDirDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "empty".getBytes());

        when(fileRepository.findFileById(9999)).thenReturn(null);
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(file, "testUser", 9999));
    }

    @Test
    void shouldThrowStorageException_whenParentDirIsNotADirectory() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "empty".getBytes());

        when(fileRepository.findFileById(9999)).thenReturn(mockFile);
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(file, "testUser", 9999));
    }

    @Test
    void shouldThrowStorageException_whenIOExceptionWhileCopyingFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenThrow(new IOException("Simulated IOException"));

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));

        File savedFile = new File("storage", "test.png", false, true, mockUser, null);
        savedFile.setId(5);
        when(fileRepository.save(any())).thenReturn(savedFile);

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(file, "testUser", null));
    }


    /**
     * Tests for when it's working
     */
    @Test
    void shouldSaveFile_whenNoParentDir() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "empty".getBytes());

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));

        File savedFile = new File("storage", "testFile.txt", false, true, mockUser, null);
        savedFile.setId(5);
        when(fileRepository.save(any())).thenReturn(savedFile);

        assertDoesNotThrow(() -> fileService.uploadFile(file, "testUser", null));
        verify(fileRepository, times(3)).save(any());
    }

    @Test
    void shouldSaveFile_whenHaveAParentDir() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "empty".getBytes());

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(mockParentDir);

        File savedFile = new File("storage", "testFile.txt", false, true, mockUser, mockParentDir);
        savedFile.setId(6);
        when(fileRepository.save(any())).thenReturn(savedFile);

        assertDoesNotThrow(() -> fileService.uploadFile(file, "testUser", 1));
        verify(fileRepository, times(3)).save(any());
    }

    /**
     * Tests for when making a directory is not working
     */
    @Test
    void shouldThrowStorageException_whenUserDoesNotExistForDirectories() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(StorageException.class, () ->
                fileService.makeDirectory("testDirectory", "testUser", mockParentDir.getId()));
    }
    /**
     * Tests for when making a directory is working
     */
    @Test
    void shouldSaveDirectory_whenNoParentDir() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));

        File savedDir = new File("storage", "testFolder", true, true, mockUser, null);
        savedDir.setId(5);
        when(fileRepository.save(any())).thenReturn(savedDir);

        fileService.makeDirectory("testFolder", "testUser", null);

        verify(fileRepository, times(2)).save(any());
    }

    @Test
    void shouldSaveDirectory_whenParentDirExists() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(mockParentDir);

        File savedDir = new File("storage", "testFolder", true, true, mockUser, null);
        savedDir.setId(6);
        when(fileRepository.save(any())).thenReturn(savedDir);

        fileService.makeDirectory("testFolder", "testUser", 1);

        verify(fileRepository, times(2)).save(any());
    }

    /**
     * Tests for getting files
     */
}
