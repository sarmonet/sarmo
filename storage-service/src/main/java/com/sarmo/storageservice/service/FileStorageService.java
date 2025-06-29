package com.sarmo.storageservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;


@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final MinioClient minioClient;

    public FileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // -------- СТАРЫЙ МЕТОД ЗАГРУЗКИ (ОСТАВЛЯЕМ БЕЗ ИЗМЕНЕНИЙ) --------
    public void uploadFile(String bucketName, MultipartFile file, String objectName) throws Exception {
        logger.info("Uploading file {} to bucket {}", objectName, bucketName);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .build());
            logger.info("File {} uploaded successfully to bucket {}", objectName, bucketName);
        } catch (MinioException e) {
            logger.error("Error uploading file {} to bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            // Можно использовать RuntimeException для единообразия или оставить старое поведение
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during file upload {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during file upload", e);
        }
    }

    // -------- НОВАЯ ПЕРЕГРУЖЕННАЯ ВЕРСИЯ МЕТОДА ЗАГРУЗКИ (С МЕТАДАННЫМИ) --------
    public void uploadFile(String bucketName, MultipartFile file, String objectName, Map<String, String> metadata) throws Exception {
        logger.info("Uploading file {} to bucket {} with metadata", objectName, bucketName);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .userMetadata(metadata != null ? metadata : Collections.emptyMap()) // Передаем метаданные
                    .build());
            logger.info("File {} uploaded successfully to bucket {} with metadata", objectName, bucketName);
        } catch (MinioException e) {
            logger.error("Error uploading file {} to bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to storage", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during file upload {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during file upload", e);
        }
    }

    // -------- СТАРЫЙ МЕТОД СКАЧИВАНИЯ (ОСТАВЛЯЕМ БЕЗ ИЗМЕНЕНИЙ, КРОМЕ ОБРАБОТКИ 404) --------
    // DocumentService будет вызывать этот метод для получения InputStream
    public InputStream downloadFile(String bucketName, String objectName) throws Exception {
        logger.info("Downloading file {} from bucket {}", objectName, bucketName);
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            logger.info("File {} downloaded successfully from bucket {}", objectName, bucketName);
            return inputStream;
        } catch (ErrorResponseException e) {
            // Специальная обработка для 404 Not Found, чтобы DocumentService мог ее поймать
            if (e.response().code() == 404) {
                logger.warn("File not found during download: {} in bucket {}", objectName, bucketName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found in storage", e);
            }
            logger.error("Minio error downloading file {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to download file from storage", e);
        } catch (MinioException e) {
            logger.error("Minio error downloading file {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to download file from storage", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during file download {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during file download", e);
        }
    }

    // -------- НОВЫЙ МЕТОД ДЛЯ ПОЛУЧЕНИЯ МЕТАДАННЫХ --------
    // DocumentService будет вызывать этот метод для получения метаданных
    public Map<String, String> getObjectMetadata(String bucketName, String objectName) throws Exception {
        logger.info("Getting metadata for object {} in bucket {}", objectName, bucketName);
        try {
            io.minio.StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            // Возвращаем пользовательские метаданные
            return new HashMap<>(stat.userMetadata());

        } catch (ErrorResponseException e) {
            // Специальная обработка для 404 Not Found
            if (e.response().code() == 404) {
                logger.warn("Metadata not found for object: {} in bucket {}", objectName, bucketName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found for metadata retrieval", e);
            }
            logger.error("Minio error getting metadata for object {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to get object metadata from storage", e);
        }
        catch (MinioException e) {
            logger.error("Minio error getting metadata for object {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to get object metadata from storage", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting metadata for object {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while getting metadata", e);
        }
    }

    // -------- СТАРЫЙ МЕТОД УДАЛЕНИЯ (ОСТАВЛЯЕМ БЕЗ ИЗМЕНЕНИЙ, КРОМЕ ОБРАБОТКИ 404) --------
    public void deleteFile(String bucketName, String objectName) throws Exception {
        logger.info("Deleting file {} from bucket {}", objectName, bucketName);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            logger.info("File {} deleted successfully from bucket {}", objectName, bucketName);
        } catch (ErrorResponseException e) {
            // Специальная обработка для 404 Not Found при удалении
            if (e.response().code() == 404) {
                logger.warn("File not found for deletion: {} in bucket {}", objectName, bucketName);
                // Можно либо проигнорировать, либо выбросить 404. Выбрасываем 404.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found for deletion in storage", e);
            }
            logger.error("Minio error deleting file {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from storage", e);
        }
        catch (MinioException e) {
            logger.error("Minio error deleting file {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from storage", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during file deletion {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during file deletion", e);
        }
    }

    // -------- СТАРЫЙ МЕТОД СТРИМИНГА ВИДЕО (ОСТАВЛЯЕМ С НЕБОЛЬШИМИ УЛУЧШЕНИЯМИ ОБРАБОТКИ ОШИБОК) --------
    public ResponseEntity<InputStreamResource> streamVideo(String bucketName, String objectName, HttpServletRequest request) throws Exception {
        logger.info("Streaming video {} from bucket {}", objectName, bucketName);
        try {
            GetObjectArgs.Builder builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName);

            String rangeHeader = request.getHeader("Range");

            // Объявляем start и end здесь, чтобы они были видны вне блока if
            long start = 0; // Инициализируем начальное значение
            Long end = null; // end может быть null, если диапазон до конца файла

            if (rangeHeader != null) {
                String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
                start = Long.parseLong(ranges[0]); // Присваиваем значение внешней переменной
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]); // Присваиваем значение внешней переменной
                }
                builder.offset(start);
                if (end != null) {
                    builder.length(end - start + 1);
                }
            }

            InputStream file = minioClient.getObject(builder.build());

            // Получаем информацию об объекте, чтобы узнать Content Type и размер для заголовков
            StatObjectArgs statArgs = StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
            io.minio.StatObjectResponse stat = minioClient.statObject(statArgs);

            HttpHeaders headers = new HttpHeaders();
            // Используем Content Type из Minio, если доступен, иначе fallback на video/mp4
            headers.setContentType(Optional.ofNullable(stat.contentType()).map(MediaType::valueOf).orElse(MediaType.valueOf("video/mp4")));

            ResponseEntity<InputStreamResource> response;
            if (rangeHeader != null) {
                headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
                // Корректно формируем заголовок CONTENT_RANGE
                String contentRangeValue = "bytes " + start + "-" + (end != null ? end : stat.size() - 1) + "/" + stat.size();
                headers.set(HttpHeaders.CONTENT_RANGE, contentRangeValue);

                // Корректно рассчитываем contentLength для PARTIAL_CONTENT
                long contentLength = (end != null) ? (end - start + 1) : (stat.size() - start);
                headers.setContentLength(contentLength); // Устанавливаем Content-Length для фрагмента


                response = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(new InputStreamResource(file));
            } else {
                // Если нет Range заголовка, это полный контент
                headers.setContentLength(stat.size());
                response = ResponseEntity.ok().headers(headers).body(new InputStreamResource(file));
            }

            logger.info("Video {} streamed successfully from bucket {}", objectName, bucketName);
            return response;

        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                logger.warn("Video not found for streaming: {} in bucket {}", objectName, bucketName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found in storage", e);
            }
            logger.error("Minio error streaming video {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to stream video from storage", e);
        }
        catch (MinioException e) {
            logger.error("Minio error streaming video {} from bucket {}: {}", objectName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to stream video from storage", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during video streaming {}: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during video streaming", e);
        }
    }
}