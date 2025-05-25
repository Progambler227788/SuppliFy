package com.supplify.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileProcessingService {
    List<String> fileList();

    String uploadFile(MultipartFile file);

    Resource downloadFile(String fileName);
}