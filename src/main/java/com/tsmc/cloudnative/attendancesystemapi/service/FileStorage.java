package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.FileUploadResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {
    FileUploadResponseDTO saveFile(MultipartFile file) throws IOException;
    Resource getFileAsResource(String fileName, Authentication authentication);
    boolean deleteFile(String fileName);
}
