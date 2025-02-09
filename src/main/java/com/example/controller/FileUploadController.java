package com.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.service.FileUploadService;
import com.example.dto.common.ResponseDto;
import com.example.dto.upload.FileUploadDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @PostMapping("/preview")
    public ResponseDto<FileUploadDto.PreviewResponse> previewExcelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "10") int previewRows) {
        try {
            FileUploadDto.PreviewResponse preview = fileUploadService.previewExcelFile(file, previewRows);
            return ResponseDto.successWithDataAndMessage(preview, "파일 미리보기 성공");
        } catch (Exception e) {
            return ResponseDto.error(e.getMessage(), "PREVIEW_FAIL");
        }
    }
    
    @PostMapping("/import")
    public ResponseDto<FileUploadDto.ImportResponse> importExcelData(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") FileUploadDto.ImportRequest request) {
        try {
            FileUploadDto.ImportResponse response = fileUploadService.importExcelData(file, request);
            return ResponseDto.successWithDataAndMessage(response, "데이터 이관 성공");
        } catch (Exception e) {
            return ResponseDto.error(e.getMessage(), "IMPORT_FAIL");
        }
    }
} 