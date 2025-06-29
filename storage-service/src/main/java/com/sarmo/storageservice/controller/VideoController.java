package com.sarmo.storageservice.controller;

import com.sarmo.storageservice.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/storage/video")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = videoService.uploadVideo(file);
            return ResponseEntity.ok(videoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping("/{objectName}")
    public ResponseEntity<InputStreamResource> downloadVideo(@PathVariable String objectName) {
        try {
            return videoService.downloadVideo(objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/stream/{objectName}")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable String objectName, HttpServletRequest request) {
        try {
            return videoService.streamVideo(objectName, request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{objectName}")
    public ResponseEntity<String> deleteVideo(@PathVariable String objectName) {
        try {
            videoService.deleteVideo(objectName);
            return ResponseEntity.ok("Video deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete video: " + e.getMessage());
        }
    }

    @PutMapping("/{objectName}")
    public ResponseEntity<String> updateVideo(@RequestParam("file") MultipartFile file, @PathVariable String objectName) {
        try {
            videoService.updateVideo(file, objectName);
            return ResponseEntity.ok("Video updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video: " + e.getMessage());
        }
    }
}