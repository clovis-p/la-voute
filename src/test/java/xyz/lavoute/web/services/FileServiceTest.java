package xyz.lavoute.web.services;

import org.aspectj.util.Reflection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.lavoute.web.dto.FileDownloadDTO;
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        ReflectionTestUtils.setField(fileService, "storageRoot", "storage");

        mockUser = new User("TestUser", "Test", "User", "Password1!");
        mockParentDir = new File("storage", "testFolder", true, false, mockUser, null);
        mockParentDir.setId(1);
        mockFile = new File("storage", "testFolder", false, false, mockUser, null);
    }

    /**
     * Tests for all the possible exceptions when uploading a file
     */
    @Test
    void shouldThrowStorageException_whenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(StorageException.class, () ->
                fileService.uploadFile(emptyFile, "testuser", mockUser.getId()));
    }

    @Test
    void shouldThrowStorageException_whenOriginalFileNameIsBlank() {
        MockMultipartFile nullNameFile = new MockMultipartFile("file", null, "text/plain", "empty".getBytes());

        assertThrows(StorageException.class, () -> {
            fileService.uploadFile(nullNameFile, "testuser", mockUser.getId());
        });
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
     * Tests for when it's working (uploading a file)
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
     * Tests for getting files (exceptions)
     */
    @Test
    void shouldThrowStorageException_whenUserDoesNotExist() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(StorageException.class, () ->
                fileService.obtainFilesFromSpecificDirectory("testUser", null));
    }

    @Test
    void shouldThrowStorageException_whenParentDirIsNotOwnedByUser() {
        User otherUser = new User();
        otherUser.setUsername("testUser");
        otherUser.setId(99);

        File parentDir = new File();
        parentDir.setUser(otherUser);
        parentDir.setIsDirectory(true);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findFileById(4)).thenReturn(parentDir);

        assertThrows(StorageException.class, () ->
                fileService.obtainFilesFromSpecificDirectory("testUser", 4));
    }

    /**
     * Tests for getting files (when it works)
     */
    @Test
    void shouldObtainFilesFromRootDir_whenParentDirIdIsNull() {
        File mockFile = new File("storage", "testFile.txt", false, true, mockUser, null);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findAllByParentDirAndUser(null, mockUser)).thenReturn(List.of(mockFile));

        Collection<FileGetDTO> result = fileService.obtainFilesFromSpecificDirectory("testUser", null);

        assertEquals(1, result.size());
    }

    @Test
    void shouldObtainFilesFromSpecificDirectory_whenGivenValidParentDirId() {
        File parentDir = new File();
        parentDir.setUser(mockUser);
        parentDir.setIsDirectory(true);

        File mockFile = new File("storage", "testFile.txt", false, true, mockUser, parentDir);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findFileById(4)).thenReturn(parentDir);
        when(fileRepository.findAllByParentDirAndUser(parentDir, mockUser)).thenReturn(List.of(mockFile));

        Collection<FileGetDTO> result = fileService.obtainFilesFromSpecificDirectory("testUser", 4);

        assertEquals(2, result.size());
    }

    @Test
    void shouldIncludeGrandParentDir_whenParentDirHasAParent() {
        File grandParentDir = new File("storage", "grandparent", true, false, mockUser, null);
        File parentDir = new File("storage", "parent", true, false, mockUser, grandParentDir);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.ofNullable(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(parentDir);
        when(fileRepository.findAllByParentDirAndUser(parentDir, mockUser)).thenReturn(List.of());

        Collection<FileGetDTO> result = fileService.obtainFilesFromSpecificDirectory("testUser", 1);

        assertEquals(1, result.size());
        assertEquals("../", result.iterator().next().getName());
    }

    @Test
    void shouldAppendSlash_whenFileIsADirectory() {
        File parentDir = new File("storage", "parent", true, false, mockUser, null);
        File subDir = new File("storage", "subFolder", true, false, mockUser, parentDir);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(parentDir);
        when(fileRepository.findAllByParentDirAndUser(parentDir, mockUser)).thenReturn(List.of(subDir));

        Collection<FileGetDTO> result = fileService.obtainFilesFromSpecificDirectory("testUser", 1);

        // "../" + "subFolder/"
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(fileGetDTO -> fileGetDTO.getName().equals("../")));
    }
    /**
     * Tests for loading a file as resource when it's not working (exceptions)
     */
    @Test
    void shouldThrowStorageException_whenFileDoesNotExistOnDisk() {
        File fileEntity = new File("storage", "missing.txt", false, false, mockUser, null);
        fileEntity.setId(1);
        fileEntity.setPath("nonExistentPath/missing.txt");

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(fileEntity);

        ReflectionTestUtils.setField(fileService, "storageRoot", "/nonexistent");

        assertThrows(StorageException.class, () ->
                fileService.loadFileAsResource("testUser", 1));
    }

    @Test
    void shouldThrowStorageException_whenPathTraversalDetected(@TempDir Path tempDir) {
        File fileEntity = new File(tempDir.toString(), "malicious.txt", false, false, mockUser, null);
        fileEntity.setId(1);
        fileEntity.setPath("../../etc/passwd");

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(fileEntity);

        ReflectionTestUtils.setField(fileService, "storageRoot", tempDir.toString());

        assertThrows(StorageException.class, () ->
                fileService.loadFileAsResource("testUser", 1));
    }

    /**
     * Tests for loading a file as resource when it works
     */
    @Test
    void shouldReturnFileDownloadDTO_whenFileExistsOnDisk(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("testFile.txt");
        Files.writeString(tempFile, "test content");

        File fileEntity = new File(tempDir.toString(), "testFile.txt", false, false, mockUser, null);
        fileEntity.setId(1);
        fileEntity.setPath("testFile.txt");

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(fileEntity);

        ReflectionTestUtils.setField(fileService, "storageRoot", tempDir.toString());

        FileDownloadDTO result = fileService.loadFileAsResource("testUser", 1);

        assertNotNull(result);
        assertEquals("testFile.txt", result.getFileName());
        assertTrue(result.getResource().exists());
    }

    @Test
    void shouldUseDefaultMimeType_whenMimeTypeIsUnknown(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("testFile.xyz");
        Files.writeString(tempFile, "test content");

        File fileEntity = new File(tempDir.toString(), "testFile.xyz", false, false, mockUser, null);
        fileEntity.setId(1);
        fileEntity.setPath("testFile.xyz");

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(fileEntity);

        ReflectionTestUtils.setField(fileService, "storageRoot", tempDir.toString());

        FileDownloadDTO result = fileService.loadFileAsResource("testUser", 1);

        assertEquals("application/octet-stream", result.getMimeType());
    }
}
