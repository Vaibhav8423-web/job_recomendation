package com.App.JobRecommender.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * Uploads a file to Cloudinary
     * @param file The file to upload
     * @param fileName The name to give the file
     * @return The URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String fileName);
}
