package com.sarmo.contentservice.dto;

import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.entity.ArticleComment;
import com.sarmo.contentservice.entity.Content;

import java.util.List;

public class ArticleFullDTO {
    private Article article;
    private Content content;
    private List<Article> relatedArticles;

    public ArticleFullDTO() {}

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<Article> getRelatedArticles() {
        return relatedArticles;
    }

    public void setRelatedArticles(List<Article> relatedArticles) {
        this.relatedArticles = relatedArticles;
    }
}