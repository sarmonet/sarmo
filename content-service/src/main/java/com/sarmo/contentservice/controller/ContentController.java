//package com.sarmo.contentservice.controller;
//
//import com.sarmo.contentservice.entity.Content;
//import com.sarmo.contentservice.service.ContentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/content/mongo")
//public class ContentController {
//
//    private final ContentService contentService;
//
//    @Autowired
//    public ContentController(ContentService contentService) {
//        this.contentService = contentService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Content>> getAllContents() {
//        try {
//            List<Content> contents = contentService.getAllContents();
//            return ResponseEntity.ok(contents);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Content> getContentById(@PathVariable String id) {
//        try {
//            Optional<Content> content = contentService.getContentById(id);
//            return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<Content> createContent(@RequestBody Content content) {
//        try {
//            Content createdContent = contentService.createContent(content);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdContent);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Content> updateContent(@PathVariable String id, @RequestBody Content contentDetails) {
//        try {
//            Content updatedContent = contentService.updateContent(id, contentDetails);
//            if (updatedContent != null) {
//                return ResponseEntity.ok(updatedContent);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
//        try {
//            contentService.deleteContent(id);
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//}