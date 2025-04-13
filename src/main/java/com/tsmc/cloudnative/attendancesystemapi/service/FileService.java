package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.common.FileAccessDeniedException;
import com.tsmc.cloudnative.attendancesystemapi.common.FileNotFoundException;
import com.tsmc.cloudnative.attendancesystemapi.common.InvalidFileTypeException;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final LeaveApplicationRepository leaveApplicationRepository;

    public String saveFile(MultipartFile file) throws IOException {
        // 檢查檔案類型
        validateFileType(file);

        // 確保目錄存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

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

        // 儲存檔案
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        log.info("檔案已上傳: {}", uniqueFileName);

        // 返回檔案名（不包含路徑）
        return uniqueFileName;
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


    public Resource getFileAsResource(String fileName, Authentication authentication) {
        try {
            log.debug("fileName: {}", fileName);
            // 檢查用戶權限
            checkUserFileAccess(fileName, authentication);

            // 獲取檔案
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                log.error("檔案不存在: {}", fileName);
                throw new FileNotFoundException("檔案不存在");
            }

            return resource;

        } catch (IOException e) {
            log.error("獲取檔案時發生錯誤: {}", e.getMessage());
            throw new FileNotFoundException("讀取檔案時發生錯誤: " + e.getMessage());
        }
    }


    private void checkUserFileAccess(String fileName, Authentication authentication) {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        if (!Files.exists(filePath)) {
            log.error("檔案不存在: {}", fileName);
            throw new FileNotFoundException("檔案不存在");
        }

        String employeeCode = authentication.getName();

//        // Admin可以訪問所有檔案
//        boolean isManager = authentication.getAuthorities().contains(
//                new SimpleGrantedAuthority("ROLE_ADMIN"));
//
//        if (isManager) {
//            log.debug("管理者 {} 訪問檔案 {}", employeeCode, fileName);
//            return;
//        }

        // 檢查該檔案是否屬於當前用戶的請假申請
        boolean hasAccess = leaveApplicationRepository.existsByFilePathAndEmployeeEmployeeCode(
                fileName, employeeCode);

        if (!hasAccess) {
            log.warn("用戶 {} 嘗試無權訪問檔案 {}", employeeCode, fileName);
            throw new FileAccessDeniedException("您無權訪問此檔案");
        }

        log.debug("用戶 {} 成功訪問檔案 {}", employeeCode, fileName);
    }


    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("刪除檔案時發生錯誤: {}", e.getMessage());
            return false;
        }
    }
}