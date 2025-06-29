package com.sarmo.chatservice.controller;

import com.sarmo.chatservice.service.MessageService;
import com.sarmo.chatservice.exception.MessageNotFoundException;
import com.sarmo.chatservice.exception.ChatNotFoundException;
import com.sarmo.chatservice.exception.PermissionDeniedException;

import com.sarmo.chatservice.dto.ChatMessageDTO; // Используем ChatMessageDTO напрямую из сервиса
import com.sarmo.chatservice.dto.MessageUpdateRequestDTO; // Только для update

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException; // Возможно, Service может бросить при получении данных User


@RestController
@RequestMapping("/api/v1/chat")
public class MessageController {

    private final MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping("/message/{id}")
    @PreAuthorize("isAuthenticated()") // Пользователь должен быть аутентифицирован и быть участником чата
    public ResponseEntity<ChatMessageDTO> getMessageById(@PathVariable String id) {
        logger.info("Getting message by ID: {}", id);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис возвращает ChatMessageDTO
            ChatMessageDTO messageDTO = messageService.getMessageById(id, currentUserId);
            logger.info("Message with ID {} found", id);
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (MessageNotFoundException e) {
            logger.warn("Message with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ChatNotFoundException e) { // Ловим, если чат, к которому относится сообщение, не найден или пользователь не участник
            logger.warn("Get message failed: Chat related to message ID {} not found or user {} not participant: {}", id, currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Или FORBIDDEN, если чат есть, но пользователь не участник
        } catch (PermissionDeniedException e) {
            logger.warn("Get message failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) { // Может быть брошено, если отправитель не найден в userService
            logger.error("Error retrieving sender details for message {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) { // Общий отлов для других непредвиденных ошибок
            logger.error("Error getting message by ID {} for user {}: {}", id, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{chatId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByChatId(@PathVariable Long chatId) {
        logger.info("Getting messages for chat ID: {}", chatId);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис возвращает List<ChatMessageDTO>
            List<ChatMessageDTO> messagesDTO = messageService.getMessagesByChatId(chatId, currentUserId);
            logger.info("Found {} messages for chat ID {}", messagesDTO.size(), chatId);
            return new ResponseEntity<>(messagesDTO, HttpStatus.OK);
        } catch (ChatNotFoundException e) {
            logger.warn("Get messages by chat ID failed: Chat with ID {} not found: {}", chatId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Get messages by chat ID failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            logger.error("Error retrieving sender details for messages in chat {}: {}", chatId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error getting messages for chat ID {} for user {}: {}", chatId, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/message/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDTO> updateMessage(@PathVariable String id, @RequestBody MessageUpdateRequestDTO messageDetailsDTO) {
        logger.info("Updating message with ID: {} from DTO", id);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис ожидает String newContent, извлекаем из DTO
            // Сервис возвращает ChatMessageDTO
            ChatMessageDTO updatedMessageDTO = messageService.editMessage(id, messageDetailsDTO.getContent(), currentUserId);
            logger.info("Message with ID {} updated by user {}", id, currentUserId);
            return new ResponseEntity<>(updatedMessageDTO, HttpStatus.OK);
        } catch (MessageNotFoundException e) {
            logger.warn("Update message failed. Message with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Update message failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ChatNotFoundException e) { // Ловим, если чат, к которому относится сообщение, не найден или пользователь не участник
            logger.warn("Update message failed: Chat related to message ID {} not found or user {} not participant: {}", id, currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Или FORBIDDEN
        } catch (NoSuchElementException e) {
            logger.error("Error retrieving sender details during message update {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error updating message with ID {} by user {}: {}", id, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/message/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMessage(@PathVariable String id) {
        logger.info("Deleting message with ID: {}", id);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис возвращает void
            messageService.deleteMessage(id, currentUserId);
            logger.info("Message with ID {} deleted by user {}", id, currentUserId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (MessageNotFoundException e) {
            logger.warn("Delete message failed. Message with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Delete message failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ChatNotFoundException e) { // Ловим, если чат, к которому относится сообщение, не найден или пользователь не участник
            logger.warn("Delete message failed: Chat related to message ID {} not found or user {} not participant: {}", id, currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Или FORBIDDEN
        } catch (Exception e) {
            logger.error("Error deleting message with ID {} by user {}: {}", id, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
                throw new IllegalStateException("Authenticated principal's name is not a valid user ID format", e);
            }
        }
        logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous");
        throw new IllegalStateException("User is not authenticated or principal is not as expected");
    }
}