package com.sarmo.contentservice.repository;

import com.sarmo.contentservice.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findAllByArticleId(Long articleId);
}