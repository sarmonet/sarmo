package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.entity.News;
import com.sarmo.contentservice.service.RandomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content/random")
public class RandomController {

    private final RandomService randomService;

    @Autowired
    public RandomController(RandomService randomService) {
        this.randomService = randomService;
    }

    @GetMapping("/news")
    public ResponseEntity<List<News>> getRandomNews(@RequestParam(defaultValue = "1") int count) {
        try {
            List<News> randomNews = randomService.getRandomNews(count);
            return ResponseEntity.ok(randomNews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/article")
    public ResponseEntity<List<Article>> getRandomArticles(@RequestParam(defaultValue = "1") int count) {
        try {
            List<Article> randomArticles = randomService.getRandomArticles(count);
            return ResponseEntity.ok(randomArticles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}