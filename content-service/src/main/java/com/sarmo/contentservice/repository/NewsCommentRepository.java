package com.sarmo.contentservice.repository;

import com.sarmo.contentservice.entity.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
    List<NewsComment> findAllByNewsId(Long newsId);
}