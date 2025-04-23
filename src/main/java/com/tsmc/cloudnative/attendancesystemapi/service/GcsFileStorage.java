package com.tsmc.cloudnative.attendancesystemapi.service;

import com.google.cloud.storage.*;
import com.tsmc.cloudnative.attendancesystemapi.common.FileNotFoundException;
import com.tsmc.cloudnative.attendancesystemapi.common.InvalidFileTypeException;
import com.tsmc.cloudnative.attendancesystemapi.dto.FileUploadResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class GcsFileStorage implements FileStorage{

    @Value("${gcs.bucket-name}")
    private String bucketName;

    private final LeaveApplicationRepository leaveApplicationRepository;

    // 初始化GCS客戶端
    private final Storage storage;

    @Autowired
    public GcsFileStorage(LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public FileUploadResponseDTO saveFile(MultipartFile file) throws IOException {
        // 檢查檔案類型
        validateFileType(file);

        // 取得原始檔案名
        String originalFileName = file.getOriginalFilename();

        // 生成唯一檔案名（避免衝突）
        String uniqueFileName = UUID.randomUUID().toString();
        if (file.getOriginalFilename() != null) {
            // 保留原始檔案副檔名
            String extension = "";
            int lastDot = file.getOriginalFilename().lastIndexOf('.');
            if (lastDot > 0) {
                extension = file.getOriginalFilename().substring(lastDot);
            }
            uniqueFileName += extension;
        }

        // 上傳到GCS
        BlobId blobId = BlobId.of(bucketName, uniqueFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        log.info("檔案已上傳到GCS: {}, 原始檔名: {}", uniqueFileName, originalFileName);


        return new FileUploadResponseDTO(uniqueFileName, originalFileName);
    }

    private void validateFileType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("檔案不能為空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidFileTypeException("檔案名稱不能為空");
        }

        // 檢查副檔名
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot + 1).toLowerCase();
        }

        Set<String> allowedExtensions = new HashSet<>(Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp",
                "pdf"
        ));

        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileTypeException("不支援的檔案類型，僅允許上傳圖片(jpg, jpeg, png, gif, bmp)和PDF檔案");
        }

        // 也可以檢查MIME類型
        String contentType = file.getContentType();
        if (contentType != null) {
            boolean isAllowedContentType = contentType.startsWith("image/") ||
                    contentType.equals("application/pdf");
            if (!isAllowedContentType) {
                throw new InvalidFileTypeException("不支援的檔案類型，僅允許上傳圖片和PDF檔案");
            }
        }
    }

    @Override
    public Resource getFileAsResource(String fileName, Authentication authentication) {
        try {
            // 檢查用戶權限(取消)

            // 從GCS獲取檔案
            Blob blob = storage.get(BlobId.of(bucketName, fileName));

            if (blob == null || !blob.exists()) {
                log.error("檔案不存在: {}", fileName);
                throw new FileNotFoundException("檔案不存在");
            }
            // 將檔案內容轉換為Resource
            byte[] content = blob.getContent();
            ByteArrayResource resource = new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            return resource;

        } catch (Exception e) {
            log.error("獲取檔案時發生錯誤: {}", e.getMessage());
            throw new FileNotFoundException("讀取檔案時發生錯誤: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        try {
            // 從GCS刪除檔案
            BlobId blobId = BlobId.of(bucketName, fileName);
            return storage.delete(blobId);
        } catch (Exception e) {
            log.error("刪除檔案時發生錯誤: {}", e.getMessage());
            return false;
        }
    }
}