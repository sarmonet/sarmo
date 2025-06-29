package com.sarmo.contentservice.repository;

import com.sarmo.contentservice.entity.News;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query(value = "SELECT * FROM news ORDER BY random() LIMIT :count", nativeQuery = true)
    List<News> findRandomNews(@Param("count") int count);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.viewCount = n.viewCount + :views WHERE n.id = :newsId")
    void incrementViewCount(@Param("newsId") Long newsId, @Param("views") Long views);

}