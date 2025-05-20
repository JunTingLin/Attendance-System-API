package com.tsmc.cloudnative.attendancesystemapi.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.tsmc.cloudnative.attendancesystemapi.common.FileNotFoundException;
import com.tsmc.cloudnative.attendancesystemapi.common.InvalidFileTypeException;
import com.tsmc.cloudnative.attendancesystemapi.dto.FileUploadResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class GcsFileStorageTest {

    @InjectMocks
    private GcsFileStorage gcsFileStorage;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private Storage storage;

    @Mock
    private Authentication authentication;

    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() throws Exception {
        // 用反射設定 private @Value 欄位 bucketName
        java.lang.reflect.Field field = GcsFileStorage.class.getDeclaredField("bucketName");
        field.setAccessible(true);
        field.set(gcsFileStorage, BUCKET_NAME);
    }

    @Test
    void saveFile_success() throws IOException {
        // 準備一個合法的檔案（jpg）
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg",
                "file content".getBytes(StandardCharsets.UTF_8));

        // 模擬 storage.create() 回傳 BlobInfo (這邊無實際用到回傳值)
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mock(Blob.class));

        FileUploadResponseDTO response = gcsFileStorage.saveFile(file);

        assertNotNull(response);
        assertEquals("test.jpg", response.getOriginalFileName());
        assertNotNull(response.getUniqueFileName());
        assertTrue(response.getUniqueFileName().endsWith(".jpg"));

        // 驗證 storage.create() 被呼叫過，且 blobId bucketName 正確
        ArgumentCaptor<BlobInfo> blobInfoCaptor = ArgumentCaptor.forClass(BlobInfo.class);
        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(storage).create(blobInfoCaptor.capture(), bytesCaptor.capture());

        BlobInfo capturedBlobInfo = blobInfoCaptor.getValue();
        assertEquals(BUCKET_NAME, capturedBlobInfo.getBlobId().getBucket());
        assertTrue(capturedBlobInfo.getBlobId().getName().endsWith(".jpg"));
        assertEquals("image/jpeg", capturedBlobInfo.getContentType());

        // 驗證上傳的 bytes 與檔案內容相同
        assertArrayEquals("file content".getBytes(StandardCharsets.UTF_8), bytesCaptor.getValue());
    }

    @Test
    void saveFile_invalidFileType_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "some text".getBytes(StandardCharsets.UTF_8));

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
                () -> gcsFileStorage.saveFile(file));
        assertTrue(exception.getMessage().contains("不支援的檔案類型"));
        verifyNoInteractions(storage);
    }

    @Test
    void saveFile_emptyFile_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg",
                new byte[0]);

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
                () -> gcsFileStorage.saveFile(file));
        assertTrue(exception.getMessage().contains("檔案不能為空"));
        verifyNoInteractions(storage);
    }

    @Test
    void getFileAsResource_success() {
        String fileName = "file123.pdf";

        Blob blob = mock(Blob.class);
        byte[] content = "file content".getBytes(StandardCharsets.UTF_8);

        when(storage.get(BlobId.of(BUCKET_NAME, fileName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.getContent()).thenReturn(content);

        Resource resource = gcsFileStorage.getFileAsResource(fileName, authentication);

        assertNotNull(resource);
        assertEquals(fileName, resource.getFilename());
        try {
            byte[] resourceContent = resource.getInputStream().readAllBytes();
            assertArrayEquals(content, resourceContent);
        } catch (IOException e) {
            fail("Reading resource input stream failed");
        }
    }

    @Test
    void getFileAsResource_fileNotFound_throwsException() {
        String fileName = "missingfile.png";

        when(storage.get(BlobId.of(BUCKET_NAME, fileName))).thenReturn(null);

        FileNotFoundException ex = assertThrows(FileNotFoundException.class,
                () -> gcsFileStorage.getFileAsResource(fileName, authentication));
        assertTrue(ex.getMessage().contains("檔案不存在"));
    }

    @Test
    void getFileAsResource_blobNotExist_throwsException() {
        String fileName = "missingfile.png";

        Blob blob = mock(Blob.class);
        when(storage.get(BlobId.of(BUCKET_NAME, fileName))).thenReturn(blob);
        when(blob.exists()).thenReturn(false);

        FileNotFoundException ex = assertThrows(FileNotFoundException.class,
                () -> gcsFileStorage.getFileAsResource(fileName, authentication));
        assertTrue(ex.getMessage().contains("檔案不存在"));
    }

    @Test
    void getFileAsResource_storageThrowsException_throwsFileNotFoundException() {
        String fileName = "errorfile.png";

        when(storage.get(BlobId.of(BUCKET_NAME, fileName))).thenThrow(new RuntimeException("GCS error"));

        FileNotFoundException ex = assertThrows(FileNotFoundException.class,
                () -> gcsFileStorage.getFileAsResource(fileName, authentication));
        assertTrue(ex.getMessage().contains("讀取檔案時發生錯誤"));
    }

    @Test
    void deleteFile_success() {
        String fileName = "deletefile.jpg";

        when(storage.delete(BlobId.of(BUCKET_NAME, fileName))).thenReturn(true);

        boolean result = gcsFileStorage.deleteFile(fileName);
        assertTrue(result);

        verify(storage).delete(BlobId.of(BUCKET_NAME, fileName));
    }

    @Test
    void deleteFile_failureReturnsFalse() {
        String fileName = "deletefile.jpg";

        when(storage.delete(BlobId.of(BUCKET_NAME, fileName))).thenReturn(false);

        boolean result = gcsFileStorage.deleteFile(fileName);
        assertFalse(result);
    }

    @Test
    void deleteFile_storageThrowsException_returnsFalse() {
        String fileName = "deletefile.jpg";

        when(storage.delete(BlobId.of(BUCKET_NAME, fileName))).thenThrow(new RuntimeException("GCS error"));

        boolean result = gcsFileStorage.deleteFile(fileName);
        assertFalse(result);
    }
}
