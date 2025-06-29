package com.sarmo.chatservice.repository;

import com.sarmo.chatservice.entity.Chat;
import com.sarmo.chatservice.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserIdsContaining(Long userId);
    Optional<Chat> findByTypeAndUserIdsContainingAndUserIdsContaining(ChatType type, Long userId1, Long userId2);

}