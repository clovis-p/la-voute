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
import xyz.lavoute.web.dto.FileGetDTO;
import xyz.lavoute.web.exceptions.StorageException;
import xyz.lavoute.web.models.File;
import xyz.lavoute.web.models.User;
import xyz.lavoute.web.repositories.FileRepository;
import xyz.lavoute.web.repositories.UserRepository;

import java.io.IOException;
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
    void shouldThrowStorageException_whenOriginalFileNameIsNull() {
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

    /**
     * Tests for renaming a file and it's not working
     */

    @Test
    void shouldThrowStorageException_whenRenamingAndUserDoesNotExist() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(StorageException.class, () ->
                fileService.renameFile(1, "testUser", "newName"));
    }

    @Test
    void shouldThrowStorageException_whenRenamingAndFileDoesNotExist() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(9999)).thenReturn(null);

        assertThrows(StorageException.class, () ->
                fileService.renameFile(9999, "testUser", "nouveauNom"));
    }

    @Test
    void shouldThrowStorageException_whenRenamingFileAndUserDoesNotOwnFile() {
        User otherUser = new User();
        otherUser.setUsername("otherUser");
        otherUser.setId(1);

        File fileOwnedByOtherUser = new File("storage", "fichier.txt", false, true, otherUser, null);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(1)).thenReturn(fileOwnedByOtherUser);

        assertThrows(StorageException.class, () ->
                fileService.renameFile(1, "testUser", "newName"));
    }

    /**
     * Test for renaming a file (when it works)
     */
    @Test
    void shouldRenameFileWithTheExtension_whenFileIsNotADirectory() {
        File fileToRename = new File("storage", "testFile.txt", false, false, mockUser, null);
        fileToRename.setId(2);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(2)).thenReturn(fileToRename);

        FileGetDTO result = fileService.renameFile(2, "testUser", "newName");

        assertEquals("newName.txt", fileToRename.getName());
        verify(fileRepository).save(fileToRename);
        assertNotNull(result);
    }

    @Test
    void shouldRenameFileWithoutExtension_whenFileIsADirectory() {
        File dirToRename = new File("storage", "folder", true, false, mockUser, null);
        dirToRename.setId(3);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(3)).thenReturn(dirToRename);

        FileGetDTO result = fileService.renameFile(3, "testUser", "newFolderName");

        assertEquals("newFolderName", dirToRename.getName());
        verify(fileRepository).save(dirToRename);
        assertNotNull(result);
    }

    /**
     * Tests for deleting a file or directory
     */
    @Test
    void shouldDeleteFile_whenFileHasNoChildren() {
        File fileToDelete = new File("storage", "testFile.txt", false, false, mockUser, null);
        fileToDelete.setId(4);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(4)).thenReturn(fileToDelete);
        when(fileRepository.findAllByParentDirAndUser(fileToDelete, mockUser)).thenReturn(List.of());

        fileService.deleteFile(4, "testUser");

        verify(fileRepository).delete(fileToDelete);
    }

    @Test
    void shouldDeleteRecursively_whenDirectoryHasChildren() {
        File parentDir = new File("storage", "folder", true, false, mockUser, null);
        parentDir.setId(2);

        File child1 = new File("storage", "child1.txt", false, true, mockUser, parentDir);
        child1.setId(3);
        File child2 = new File("storage", "child2.txt", false, true, mockUser, parentDir);
        child2.setId(4);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(fileRepository.findFileById(2)).thenReturn(parentDir);
        when(fileRepository.findAllByParentDirAndUser(parentDir, mockUser)).thenReturn(List.of(child1, child2));
        when(fileRepository.findAllByParentDirAndUser(child1, mockUser)).thenReturn(List.of());
        when(fileRepository.findAllByParentDirAndUser(child2, mockUser)).thenReturn(List.of());

        fileService.deleteFile(2, "testUser");

        //The children are deleted before the parent
        verify(fileRepository).delete(child1);
        verify(fileRepository).delete(child2);
        verify(fileRepository).delete(parentDir);
    }
}
