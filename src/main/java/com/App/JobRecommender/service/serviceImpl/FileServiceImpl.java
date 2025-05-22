package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.exception.FileUploadException;
import com.App.JobRecommender.service.FileService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private final Cloudinary cloudinary;
    private static final String[] ALLOWED_FILE_TYPES = {
        "application/pdf", 
        "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    public FileServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        validateFile(file);
        
        try {
            String contentType = file.getContentType();
            boolean isDocument = contentType != null && Arrays.asList(ALLOWED_FILE_TYPES).contains(contentType);

            Map<String, Object> uploadParams;
            if (isDocument) {
                // For PDFs and documents
                uploadParams = ObjectUtils.asMap(
                    "public_id", fileName,
                    "resource_type", "raw",
                    "type", "upload"
                );
            } else {
                // For images
                uploadParams = ObjectUtils.asMap(
                    "public_id", fileName,
                    "resource_type", "image"
                );
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            String url = (String) uploadResult.get("secure_url");
            if (url == null || url.isEmpty()) {
                logger.error("Failed to get URL from Cloudinary response: {}", uploadResult);
                throw new FileUploadException("Failed to get URL from Cloudinary");
            }

            // For PDFs, ensure the URL has the correct format
            if (contentType != null && contentType.equals("application/pdf")) {
                // Remove any query parameters or fragments that might cause issues
                if (url.contains("?")) {
                    url = url.substring(0, url.indexOf("?"));
                }
                if (url.contains("#")) {
                    url = url.substring(0, url.indexOf("#"));
                }
            }

            logger.info("File successfully uploaded to Cloudinary: {}", fileName);
            return url;

        } catch (IOException e) {
            logger.error("IO error while uploading file: {}", fileName, e);
            throw new FileUploadException("Failed to read file contents: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("Cloudinary upload failed for file: {}", fileName, e);
            throw new FileUploadException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();
        // Allow both documents and images
        if (contentType == null || 
            (!contentType.startsWith("image/") && !Arrays.asList(ALLOWED_FILE_TYPES).contains(contentType))) {
            throw new IllegalArgumentException(
                "Invalid file type. Allowed types are: Images, PDF, DOC, DOCX"
            );
        }

        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
    }
}

