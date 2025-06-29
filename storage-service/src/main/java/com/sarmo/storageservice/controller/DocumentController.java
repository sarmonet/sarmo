package com.sarmo.storageservice.controller;

import com.sarmo.storageservice.service.DocumentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/storage/document")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String documentUrl = documentService.uploadDocument(file);
            return ResponseEntity.ok(documentUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload document: " + e.getMessage());
        }
    }

    @GetMapping("/{objectName}")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable String objectName) {
        try {
            return documentService.downloadDocument(objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{objectName}")
    public ResponseEntity<String> deleteDocument(@PathVariable String objectName) {
        try {
            documentService.deleteDocument(objectName);
            return ResponseEntity.ok("Document deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete document: " + e.getMessage());
        }
    }

    @PutMapping("/{objectName}")
    public ResponseEntity<String> updateDocument(@RequestParam("file") MultipartFile file, @PathVariable String objectName) {
        try {
            documentService.updateDocument(file, objectName);
            return ResponseEntity.ok("Document updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update document: " + e.getMessage());
        }
    }
}