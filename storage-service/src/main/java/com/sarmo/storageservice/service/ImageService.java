package com.sarmo.storageservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final FileStorageService fileStorageService;


    private final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/webp"
    );

    @Value("${minio.imageBucket}")
    private String bucketName;

    @Value("${app.sarmo.storage.baseUrl}")
    private String BASE_URL;

    public ImageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String uploadImage(MultipartFile file) throws Exception {
        String objectName = UUID.randomUUID().toString();
        logger.info("Uploading image {} to bucket {}", objectName, bucketName);
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 10 MB");
            }

            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
            }

            fileStorageService.uploadFile(bucketName, file, objectName);
            logger.info("Image {} uploaded successfully to bucket {}", objectName, bucketName);
            return BASE_URL + "image/" + objectName;
        } catch (Exception e) {
            logger.error("Error uploading image {} to bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public ResponseEntity<InputStreamResource> downloadImage(String objectName) throws Exception {
        logger.info("Downloading image {} from bucket {}", objectName, bucketName);
        try {
            InputStream imageStream = fileStorageService.downloadFile(bucketName, objectName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectName);

            MediaType contentType = getMediaType(objectName);

            ResponseEntity<InputStreamResource> response = ResponseEntity.ok()
                    .headers(headers)
                    .contentType(contentType)
                    .body(new InputStreamResource(imageStream));

            logger.info("Image {} downloaded successfully from bucket {}", objectName, bucketName);
            return response;
        } catch (Exception e) {
            logger.error("Error downloading image {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteImage(String objectName) throws Exception {
        logger.info("Deleting image {} from bucket {}", objectName, bucketName);
        try {
            fileStorageService.deleteFile(bucketName, objectName);
            logger.info("Image {} deleted successfully from bucket {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error deleting image {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public void updateImage(MultipartFile file, String objectName) throws Exception {
        logger.info("Updating image {} in bucket {}", objectName, bucketName);
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 10 MB");
            }

            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
            }

            fileStorageService.deleteFile(bucketName, objectName);
            fileStorageService.uploadFile(bucketName, file, objectName);
            logger.info("Image {} updated successfully in bucket {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error updating image {} in bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    private MediaType getMediaType(String objectName) {
        if (objectName.toLowerCase().endsWith(".jpg") || objectName.toLowerCase().endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (objectName.toLowerCase().endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (objectName.toLowerCase().endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (objectName.toLowerCase().endsWith(".webp")) {
            return MediaType.valueOf("image/webp");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}