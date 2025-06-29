package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByListingId(Long listingId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
}
