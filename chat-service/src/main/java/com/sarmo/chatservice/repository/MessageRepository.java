package com.sarmo.chatservice.repository;

import com.sarmo.chatservice.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatId(Long chatId);
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);

}