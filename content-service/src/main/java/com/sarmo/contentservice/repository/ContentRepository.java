package com.sarmo.contentservice.repository;

import com.sarmo.contentservice.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {
}