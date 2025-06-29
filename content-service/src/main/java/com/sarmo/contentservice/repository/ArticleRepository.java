package com.sarmo.contentservice.repository;

import com.sarmo.contentservice.entity.Article;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(value = "SELECT * FROM articles ORDER BY random() LIMIT :count", nativeQuery = true)
    List<Article> findRandomArticles(@Param("count") int count);

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + :views WHERE a.id = :articleId")
    void incrementViewCount(@Param("articleId") Long articleId, @Param("views") Long views);

}