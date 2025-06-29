package com.sarmo.storageservice.service;

import jakarta.servlet.http.HttpServletRequest;
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
public class VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final FileStorageService fileStorageService;

    private final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB

    private final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "video/mp4",
            "video/webm",
            "video/ogg",
            "video/quicktime" // .mov
    );

    @Value("${minio.videoBucket}")
    private String bucketName;

    @Value("${app.sarmo.storage.baseUrl}")
    private String BASE_URL;

    public VideoService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String uploadVideo(MultipartFile file) throws Exception {
        String objectName = UUID.randomUUID().toString();
        logger.info("Uploading video {} to bucket {}", objectName, bucketName);
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 100 MB");
            }

            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
            }

            fileStorageService.uploadFile(bucketName, file, objectName);
            logger.info("Video {} uploaded successfully to bucket {}", objectName, bucketName);
            return BASE_URL + "video/" + objectName;
        } catch (Exception e) {
            logger.error("Error uploading video {} to bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public ResponseEntity<InputStreamResource> downloadVideo(String objectName) throws Exception {
        logger.info("Downloading video {} from bucket {}", objectName, bucketName);
        try {
            InputStream videoStream = fileStorageService.downloadFile(bucketName, objectName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectName);

            MediaType contentType = getMediaType(objectName);

            ResponseEntity<InputStreamResource> response = ResponseEntity.ok()
                    .headers(headers)
                    .contentType(contentType)
                    .body(new InputStreamResource(videoStream));

            logger.info("Video {} downloaded successfully from bucket {}", objectName, bucketName);
            return response;
        } catch (Exception e) {
            logger.error("Error downloading video {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public ResponseEntity<InputStreamResource> streamVideo(String objectName, HttpServletRequest request) throws Exception {
        logger.info("Streaming video {} from bucket {}", objectName, bucketName);
        try {
            ResponseEntity<InputStreamResource> response = fileStorageService.streamVideo(bucketName, objectName, request);
            logger.info("Video {} streamed successfully from bucket {}", objectName, bucketName);
            return response;
        } catch (Exception e) {
            logger.error("Error streaming video {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteVideo(String objectName) throws Exception {
        logger.info("Deleting video {} from bucket {}", objectName, bucketName);
        try {
            fileStorageService.deleteFile(bucketName, objectName);
            logger.info("Video {} deleted successfully from bucket {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error deleting video {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    public void updateVideo(MultipartFile file, String objectName) throws Exception {
        logger.info("Updating video {} in bucket {}", objectName, bucketName);
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 100 MB");
            }

            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
            }

            fileStorageService.deleteFile(bucketName, objectName);
            fileStorageService.uploadFile(bucketName, file, objectName);
            logger.info("Video {} updated successfully in bucket {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error updating video {} in bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    private MediaType getMediaType(String objectName) {
        if (objectName.toLowerCase().endsWith(".mp4")) {
            return MediaType.valueOf("video/mp4");
        } else if (objectName.toLowerCase().endsWith(".webm")) {
            return MediaType.valueOf("video/webm");
        } else if (objectName.toLowerCase().endsWith(".ogg")) {
            return MediaType.valueOf("video/ogg");
        } else if (objectName.toLowerCase().endsWith(".mov")) {
            return MediaType.valueOf("video/quicktime");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}