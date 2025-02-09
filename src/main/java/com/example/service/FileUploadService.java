package com.example.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.dto.upload.FileUploadDto;

public interface FileUploadService {
    FileUploadDto.PreviewResponse previewExcelFile(MultipartFile file, int previewRows) throws Exception;
    FileUploadDto.ImportResponse importExcelData(MultipartFile file, FileUploadDto.ImportRequest request) throws Exception;
} 