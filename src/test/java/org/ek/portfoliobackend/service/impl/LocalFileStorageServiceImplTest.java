package org.ek.portfoliobackend.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LocalFileStorageServiceImpl to verify file storage operations.
 */
class LocalFileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        // Use temporary directory for tests
        storageService = new LocalFileStorageServiceImpl(tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        // Cleanup is automatic with @TempDir
    }

    @Test
    void store_WithValidFile_ShouldStoreFileSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test-image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Act
        String url = storageService.store(file);

        // Assert
        assertNotNull(url);
        assertTrue(url.startsWith("/uploads/"));
        assertTrue(url.endsWith(".jpg"));

        // Verify file exists on filesystem
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile));

        // Verify file content
        byte[] storedContent = Files.readAllBytes(storedFile);
        assertArrayEquals("test image content".getBytes(), storedContent);
    }

    @Test
    void store_WithEmptyFile_ShouldThrowIllegalArgumentException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "empty",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> storageService.store(emptyFile)
        );

        assertEquals("Cannot store empty file", exception.getMessage());
    }

    @Test
    void store_WithFileWithoutExtension_ShouldStoreSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test",
                "testfile",  // No extension
                "application/octet-stream",
                "test content".getBytes()
        );

        // Act
        String url = storageService.store(file);

        // Assert
        assertNotNull(url);
        assertTrue(url.startsWith("/uploads/"));

        // Verify file exists
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    void store_WithDifferentExtensions_ShouldPreserveExtension() throws IOException {
        // Test multiple file types
        String[] extensions = {".jpg", ".png", ".gif", ".pdf", ".txt"};

        for (String extension : extensions) {
            // Arrange
            MultipartFile file = new MockMultipartFile(
                    "test",
                    "testfile" + extension,
                    "application/octet-stream",
                    "test content".getBytes()
            );

            // Act
            String url = storageService.store(file);

            // Assert
            assertTrue(url.endsWith(extension),
                    "URL should preserve extension " + extension);

            // Cleanup for next iteration
            String filename = url.substring(url.lastIndexOf("/") + 1);
            Files.deleteIfExists(tempDir.resolve(filename));
        }
    }

    @Test
    void store_MultipleFiles_ShouldGenerateUniqueFilenames() throws IOException {
        // Arrange
        MultipartFile file1 = new MockMultipartFile("file1", "test.jpg", "image/jpeg", "content1".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "test.jpg", "image/jpeg", "content2".getBytes());

        // Act
        String url1 = storageService.store(file1);
        String url2 = storageService.store(file2);

        // Assert
        assertNotEquals(url1, url2, "Should generate unique filenames");

        // Verify both files exist
        String filename1 = url1.substring(url1.lastIndexOf("/") + 1);
        String filename2 = url2.substring(url2.lastIndexOf("/") + 1);
        assertTrue(Files.exists(tempDir.resolve(filename1)));
        assertTrue(Files.exists(tempDir.resolve(filename2)));
    }

    @Test
    void delete_WithExistingFile_ShouldDeleteSuccessfully() throws IOException {
        // Arrange - first store a file
        MultipartFile file = new MockMultipartFile("test", "test.jpg", "image/jpeg", "test content".getBytes());
        String url = storageService.store(file);

        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile), "File should exist before deletion");

        // Act
        storageService.delete(url);

        // Assert
        assertFalse(Files.exists(storedFile), "File should be deleted");
    }

    @Test
    void delete_WithNonExistentFile_ShouldNotThrowException() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> storageService.delete("/uploads/nonexistent.jpg"));
    }

    @Test
    void delete_WithNullUrl_ShouldNotThrowException() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> storageService.delete(null));
    }

    @Test
    void delete_WithEmptyUrl_ShouldNotThrowException() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> storageService.delete(""));
    }

    @Test
    void getUploadPath_ShouldReturnConfiguredPath() {
        // Act
        Path uploadPath = storageService.getUploadPath();

        // Assert
        assertNotNull(uploadPath);
        assertEquals(tempDir, uploadPath);
    }

    @Test
    void constructor_ShouldCreateUploadDirectoryIfNotExists() {
        // Arrange - create a new service with non-existent directory
        Path newDir = tempDir.resolve("new-upload-dir");
        assertFalse(Files.exists(newDir), "Directory should not exist initially");

        // Act
        LocalFileStorageServiceImpl newService = new LocalFileStorageServiceImpl(newDir.toString());

        // Assert
        assertTrue(Files.exists(newDir), "Directory should be created");
        assertTrue(Files.isDirectory(newDir), "Should be a directory");
    }

    @Test
    void store_WithLargeFile_ShouldStoreSuccessfully() throws IOException {
        // Arrange - create a 1MB file
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        MultipartFile largeFile = new MockMultipartFile(
                "large",
                "large.bin",
                "application/octet-stream",
                largeContent
        );

        // Act
        String url = storageService.store(largeFile);

        // Assert
        assertNotNull(url);
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile));

        // Verify file size
        long fileSize = Files.size(storedFile);
        assertEquals(largeContent.length, fileSize);
    }

    @Test
    void store_WithSpecialCharactersInFilename_ShouldStoreSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test",
                "test file with spaces & special (chars).jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String url = storageService.store(file);

        // Assert
        assertNotNull(url);
        assertTrue(url.startsWith("/uploads/"));
        assertTrue(url.endsWith(".jpg"));

        // UUID-based filename should handle special characters automatically
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    void store_ConcurrentCalls_ShouldHandleMultipleFilesCorrectly() throws IOException {
        // Arrange
        int numberOfFiles = 10;
        String[] urls = new String[numberOfFiles];

        // Act - store multiple files
        for (int i = 0; i < numberOfFiles; i++) {
            MultipartFile file = new MockMultipartFile(
                    "file" + i,
                    "test" + i + ".jpg",
                    "image/jpeg",
                    ("content" + i).getBytes()
            );
            urls[i] = storageService.store(file);
        }

        // Assert - all URLs should be unique
        for (int i = 0; i < numberOfFiles; i++) {
            for (int j = i + 1; j < numberOfFiles; j++) {
                assertNotEquals(urls[i], urls[j],
                        "URLs should be unique for different files");
            }

            // Verify each file exists
            String filename = urls[i].substring(urls[i].lastIndexOf("/") + 1);
            assertTrue(Files.exists(tempDir.resolve(filename)));
        }
    }

    @Test
    void store_AndDelete_FullLifecycle_ShouldWorkCorrectly() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "lifecycle-test",
                "lifecycle.jpg",
                "image/jpeg",
                "lifecycle content".getBytes()
        );

        // Act - Store
        String url = storageService.store(file);
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Path storedFile = tempDir.resolve(filename);

        // Assert - File exists after store
        assertTrue(Files.exists(storedFile));
        assertEquals("lifecycle content", new String(Files.readAllBytes(storedFile)));

        // Act - Delete
        storageService.delete(url);

        // Assert - File removed after delete
        assertFalse(Files.exists(storedFile));
    }
}