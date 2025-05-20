package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.common.FileNotFoundException;
import com.tsmc.cloudnative.attendancesystemapi.common.InvalidFileTypeException;
import com.tsmc.cloudnative.attendancesystemapi.dto.FileUploadResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class LocalFileStorageTest {

    @TempDir
    Path tempDir; // JUnit 5 自帶的臨時目錄，用來測試本地檔案操作

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LocalFileStorage localFileStorage;

    @BeforeEach
    void setUp() throws Exception {
        // 利用反射注入 private @Value 屬性 uploadDir
        var uploadDirField = LocalFileStorage.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(localFileStorage, tempDir.toString());

        // 初始化目錄（觸發 @PostConstruct）
        localFileStorage.init();
    }

    @Test
    void saveFile_success() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "dummy content".getBytes());

        FileUploadResponseDTO result = localFileStorage.saveFile(mockFile);

        assertNotNull(result);
        assertEquals("test-image.jpg", result.getOriginalFileName());
        assertNotNull(result.getUniqueFileName());
        assertTrue(result.getUniqueFileName().endsWith(".jpg"));

        // 確認檔案真的存在於臨時目錄
        Path savedFile = tempDir.resolve(result.getUniqueFileName());
        assertTrue(Files.exists(savedFile));
        byte[] content = Files.readAllBytes(savedFile);
        assertArrayEquals("dummy content".getBytes(), content);
    }

    @Test
    void saveFile_invalidFileType_throwsInvalidFileTypeException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.exe", "application/octet-stream", "exe content".getBytes());

        InvalidFileTypeException ex = assertThrows(InvalidFileTypeException.class, () ->
                localFileStorage.saveFile(mockFile));

        assertTrue(ex.getMessage().contains("不支援的檔案類型"));
    }

    @Test
    void saveFile_emptyFile_throwsInvalidFileTypeException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        InvalidFileTypeException ex = assertThrows(InvalidFileTypeException.class, () ->
                localFileStorage.saveFile(mockFile));

        assertEquals("檔案不能為空", ex.getMessage());
    }

    @Test
    void getFileAsResource_success() throws IOException {
        // 先準備檔案在臨時目錄
        String fileName = "exist-file.pdf";
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, "file content".getBytes());

        Resource resource = localFileStorage.getFileAsResource(fileName, authentication);

        assertNotNull(resource);
        assertEquals(fileName, resource.getFilename());
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void getFileAsResource_fileNotFound_throwsFileNotFoundException() {
        String fileName = "not-exist.pdf";

        FileNotFoundException ex = assertThrows(FileNotFoundException.class, () ->
                localFileStorage.getFileAsResource(fileName, authentication));

        assertTrue(ex.getMessage().contains("檔案不存在"));
    }

    @Test
    void deleteFile_success() throws IOException {
        String fileName = "delete-me.pdf";
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, "to delete".getBytes());

        boolean deleted = localFileStorage.deleteFile(fileName);

        assertTrue(deleted);
        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_notExists_returnsFalse() {
        boolean deleted = localFileStorage.deleteFile("not-exist-file.pdf");
        assertFalse(deleted);
    }
}
