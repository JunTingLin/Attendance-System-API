package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.FileUploadResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上傳檔案")
    public ApiResponse<FileUploadResponseDTO> uploadFile(
            @RequestParam("file")
            @Parameter(description = "檔案", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile file) throws IOException {
        FileUploadResponseDTO fileInfo = fileService.saveFile(file);
        return ApiResponse.success("檔案上傳成功", fileInfo);
    }


    @GetMapping("/download/{fileName:.+}")
    @Operation(summary = "下載檔案")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, Authentication authentication) {

        Resource resource = fileService.getFileAsResource(fileName, authentication);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileName:.+}")
    @Operation(summary = "刪除檔案")
    public ApiResponse<Boolean> deleteFile(
            @PathVariable String fileName,
            Authentication authentication) {

        fileService.getFileAsResource(fileName, authentication);

        boolean result = fileService.deleteFile(fileName);

        if (result) {
            return ApiResponse.success("檔案已成功刪除", true);
        } else {
            return ApiResponse.error(500, "檔案刪除失敗");
        }
    }


}