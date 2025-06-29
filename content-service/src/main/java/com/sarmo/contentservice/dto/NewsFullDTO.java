package com.sarmo.contentservice.dto;

import com.sarmo.contentservice.entity.Content;
import com.sarmo.contentservice.entity.News;
import com.sarmo.contentservice.entity.NewsComment;

import java.util.List;

public class NewsFullDTO {
    private News news;
    private Content content;
    private List<News> relatedNews;

    public NewsFullDTO() {}

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<News> getRelatedNews() {
        return relatedNews;
    }

    public void setRelatedNews(List<News> relatedNews) {
        this.relatedNews = relatedNews;
    }
}