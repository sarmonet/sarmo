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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final FileStorageService fileStorageService;

    private final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @Value("${minio.documentBucket}")
    private String bucketName;

    @Value("${app.sarmo.storage.baseUrl}")
    private String BASE_URL;

    // Keys for metadata in storage (MinIO)
    private static final String METADATA_ORIGINAL_FILENAME = "original-filename";
    private static final String METADATA_CONTENT_TYPE = "content-type";

    public DocumentService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String uploadDocument(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload empty file");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 50 MB");
        }

        String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown_file");
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(dotIndex + 1);
        }

        // Generate UUID and append extension for the storage object name
        String objectName = UUID.randomUUID().toString() + (fileExtension.isEmpty() ? "" : "." + fileExtension);

        // Prepare metadata to store with the object
        Map<String, String> metadata = new HashMap<>();
        // Store original filename as metadata
        metadata.put(METADATA_ORIGINAL_FILENAME, originalFilename);
        // Store Content Type as metadata
        metadata.put(METADATA_CONTENT_TYPE, Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));


        logger.info("Uploading document '{}' as object '{}' to bucket {}", originalFilename, objectName, bucketName);

        try {
            // fileStorageService.uploadFile should now accept metadata
            fileStorageService.uploadFile(bucketName, file, objectName, metadata);
            logger.info("Document '{}' uploaded successfully as object '{}' to bucket {}", originalFilename, objectName, bucketName);

            // Return the URL for download, using the new object name with extension
            return BASE_URL + "document/" + objectName;
        } catch (Exception e) {
            logger.error("Error uploading document '{}' as object '{}' to bucket {}: {}", originalFilename, objectName, bucketName, e.getMessage(), e);
            throw e;
        }
    }

    // downloadDocument method now takes objectName which includes UUID and extension
    public ResponseEntity<InputStreamResource> downloadDocument(String objectName) throws Exception {
        logger.info("Downloading document object '{}' from bucket {}", objectName, bucketName);

        try {
            InputStream documentStream = fileStorageService.downloadFile(bucketName, objectName);

            // Get metadata for this object
            Map<String, String> metadata = fileStorageService.getObjectMetadata(bucketName, objectName);

            // Extract original filename from metadata or use objectName as fallback
            String originalFilename = Optional.ofNullable(metadata.get(METADATA_ORIGINAL_FILENAME))
                    .orElse(objectName); // Fallback to objectName if metadata is missing

            // Encode the filename for Content-Disposition header
            String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8).replace("+", "%20");


            HttpHeaders headers = new HttpHeaders();
            // Set Content-Disposition header with the original filename
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"");

            // Extract Content Type from metadata or use APPLICATION_OCTET_STREAM as fallback
            MediaType contentType = Optional.ofNullable(metadata.get(METADATA_CONTENT_TYPE))
                    .map(MediaType::parseMediaType)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);


            ResponseEntity<InputStreamResource> response = ResponseEntity.ok()
                    .headers(headers)
                    .contentType(contentType) // Set the original Content Type
                    .body(new InputStreamResource(documentStream));

            logger.info("Document object '{}' downloaded successfully from bucket {}. Original filename: '{}'", objectName, bucketName, originalFilename);
            return response;
        } catch (Exception e) {
            logger.error("Error downloading document object '{}' from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            // Check exception message for specific errors like file not found
            if (e.getMessage() != null && e.getMessage().contains("The specified key does not exist")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found", e);
            }
            throw e;
        }
    }

    // deleteDocument takes objectName which includes the extension
    public void deleteDocument(String objectName) throws Exception {
        logger.info("Deleting document object '{}' from bucket {}", objectName, bucketName);
        try {
            fileStorageService.deleteFile(bucketName, objectName);
            logger.info("Document object '{}' deleted successfully from bucket {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error deleting document object '{}' from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("The specified key does not exist")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found for deletion", e);
            }
            throw e;
        }
    }

    // updateDocument takes objectName (UUID.extension) to identify the file to update
    // and a new file (MultipartFile) for content and metadata
    public void updateDocument(MultipartFile file, String objectName) throws Exception {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update with empty file");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds 50 MB");
        }

        String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown_file");
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(dotIndex + 1);
        }

        // Prepare metadata for the new file
        Map<String, String> metadata = new HashMap<>();
        metadata.put(METADATA_ORIGINAL_FILENAME, originalFilename);
        metadata.put(METADATA_CONTENT_TYPE, Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        logger.info("Updating document object '{}' with new file '{}' in bucket {}", objectName, originalFilename, bucketName);

        try {

            // Upload the new content using the same object name, updating metadata
            fileStorageService.uploadFile(bucketName, file, objectName, metadata);

            logger.info("Document object '{}' updated successfully with new file '{}' in bucket {}", objectName, originalFilename, bucketName);
        } catch (Exception e) {
            logger.error("Error updating document object '{}' with new file '{}' in bucket {}: {}", objectName, originalFilename, bucketName, e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("The specified key does not exist")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found for update", e);
            }
            throw e;
        }
    }
}