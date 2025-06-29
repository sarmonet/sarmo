package com.sarmo.chatservice.controller;

import com.sarmo.chatservice.service.ChatService;
import com.sarmo.chatservice.exception.ChatNotFoundException;
import com.sarmo.chatservice.exception.PermissionDeniedException;
import com.sarmo.chatservice.dto.ChatResponseDTO;
import com.sarmo.chatservice.dto.ChatCreationRequestDTO;
import com.sarmo.chatservice.dto.ChatUpdateRequestDTO;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDTO> createChat(@RequestBody ChatCreationRequestDTO chatDTO) {
        logger.info("Creating chat from DTO: {}", chatDTO);
        Long currentUserId = getCurrentUserId();
        try {
            ChatResponseDTO createdChatDTO = chatService.createChat(chatDTO, currentUserId);
            logger.info("Chat created with ID: {} by user {}", createdChatDTO.getId(), currentUserId);
            return new ResponseEntity<>(createdChatDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Chat creation failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e) {
            logger.error("Error creating chat by user {} from DTO: {}", currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDTO> getChatById(@PathVariable Long id) {
        logger.info("Getting chat by ID: {}", id);
        Long currentUserId = getCurrentUserId(); // Получаем currentUserId в контроллере
        try {
            // Передаем currentUserId в сервис
            ChatResponseDTO chatDTO = chatService.getChatById(id, currentUserId);
            logger.info("Chat with ID {} found and accessed", id);
            return new ResponseEntity<>(chatDTO, HttpStatus.OK);
        } catch (ChatNotFoundException e) {
            logger.warn("Chat with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Access to chat {} denied: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            logger.error("Error getting chat by ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatResponseDTO>> getAllChats() {
        logger.info("Getting all chats (Admin)");
        try {
            List<ChatResponseDTO> chatsDTO = chatService.getAllChats();
            logger.info("Admin successfully retrieved {} chats", chatsDTO.size());
            return new ResponseEntity<>(chatsDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error getting all chats by admin: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatResponseDTO>> getMyChats() {
        logger.info("Getting chats for current user");
        Long currentUserId = getCurrentUserId();
        try {
            // Передаем currentUserId в сервис
            List<ChatResponseDTO> chats = chatService.getChatsByUserId(currentUserId);
            logger.info("Successfully retrieved {} chats for user ID: {}", chats.size(), currentUserId);
            return new ResponseEntity<>(chats, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error getting chats for user ID {}: {}", currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDTO> updateChat(@PathVariable Long id, @RequestBody ChatUpdateRequestDTO chatDetailsDTO) {
        logger.info("Updating chat with ID: {} from DTO", id);
        Long currentUserId = getCurrentUserId();
        try {
            ChatResponseDTO updatedChatDTO = chatService.updateChat(id, chatDetailsDTO, currentUserId);
            logger.info("Chat with ID {} updated by user {}", id, currentUserId);
            return new ResponseEntity<>(updatedChatDTO, HttpStatus.OK);
        } catch (ChatNotFoundException e) {
            logger.warn("Update chat failed. Chat with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Update chat failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            logger.error("Error updating chat with ID {} by user {}: {}", id, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        logger.info("Deleting chat with ID: {}", id);
        Long currentUserId = getCurrentUserId();
        try {
            chatService.deleteChat(id, currentUserId);
            logger.info("Chat with ID {} deleted by user {}", id, currentUserId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ChatNotFoundException e) {
            logger.warn("Delete chat failed. Chat with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Delete chat failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            logger.error("Error deleting chat with ID {} by user {}: {}", id, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{chatId}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDTO> addUserToChat(@PathVariable Long chatId, @PathVariable Long userId) {
        logger.info("Current user is attempting to add user with ID {} to chat with ID {}", userId, chatId);
        Long currentUserId = getCurrentUserId();
        try {
            ChatResponseDTO updatedChatDTO = chatService.addUserToChat(chatId, userId, currentUserId);
            logger.info("User {} added to chat {} by user {}", userId, chatId, currentUserId);
            return new ResponseEntity<>(updatedChatDTO, HttpStatus.OK);
        } catch (ChatNotFoundException e) {
            logger.warn("Add user to chat failed: Chat with ID {} not found: {}", chatId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e) {
            logger.warn("Add user to chat failed: User with ID {} not found by service: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Add user to chat failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            logger.error("Error adding user {} to chat {} by user {}: {}", userId, chatId, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{chatId}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDTO> removeUserFromChat(@PathVariable Long chatId, @PathVariable Long userId) {
        logger.info("Current user is attempting to remove user with ID {} from chat with ID {}", userId, chatId);
        Long currentUserId = getCurrentUserId();
        try {
            ChatResponseDTO updatedChatDTO = chatService.removeUserFromChat(chatId, userId, currentUserId);
            logger.info("User {} removed from chat {} by user {}", userId, chatId, currentUserId);
            return new ResponseEntity<>(updatedChatDTO, HttpStatus.OK);
        } catch (ChatNotFoundException e) {
            logger.warn("Remove user from chat failed: Chat with ID {} not found: {}", chatId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e) {
            logger.warn("Remove user from chat failed: User with ID {} not found or not in chat {} as expected: {}", userId, chatId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PermissionDeniedException e) {
            logger.warn("Remove user from chat failed for user {}: {}", currentUserId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            logger.error("Error removing user {} from chat {} by user {}: {}", userId, chatId, currentUserId, e.getMessage(), e);
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