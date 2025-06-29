package com.sarmo.chatservice.controller;

import com.sarmo.chatservice.dto.ChatMessageDTO;
import com.sarmo.chatservice.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class WebSocketMessageController {

    private final MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageController.class);

    public WebSocketMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        Long currentUserId = Long.valueOf(principal.getName());
        logger.info("Received message from user {} for chat {}: {}", currentUserId, chatMessageDTO.getChatId(), chatMessageDTO.getContent());

        chatMessageDTO.setSenderId(currentUserId);

        return messageService.sendMessage(chatMessageDTO, currentUserId);
    }

    @MessageMapping("/chat.addUser")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        Long currentUserId = Long.valueOf(principal.getName());
        logger.info("User {} joined chat {}", currentUserId, chatMessageDTO.getChatId());

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", principal.getName());
        headerAccessor.getSessionAttributes().put("chatId", chatMessageDTO.getChatId());

        chatMessageDTO.setContent("User " + currentUserId + " joined the chat!");
        chatMessageDTO.setSenderId(currentUserId);
        chatMessageDTO.setTimestamp(LocalDateTime.now());

        return chatMessageDTO;
    }
}