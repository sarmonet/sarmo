package com.sarmo.storageservice.controller;

import com.sarmo.storageservice.service.ImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/storage/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<String>> uploadMultipleImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        try {
            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);
            }

            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{objectName}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String objectName) {
        try {
            return imageService.downloadImage(objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{objectName}")
    public ResponseEntity<String> deleteImage(@PathVariable String objectName) {
        try {
            imageService.deleteImage(objectName);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image: " + e.getMessage());
        }
    }

    @PutMapping("/{objectName}")
    public ResponseEntity<String> updateImage(@RequestParam("file") MultipartFile file, @PathVariable String objectName) {
        try {
            imageService.updateImage(file, objectName);
            return ResponseEntity.ok("Image updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update image: " + e.getMessage());
        }
    }
}