package com.sarmo.chatservice.service;

import com.sarmo.chatservice.entity.Message;
import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.repository.MessageRepository;
import com.sarmo.chatservice.exception.ChatNotFoundException;
import com.sarmo.chatservice.exception.MessageNotFoundException; // Добавляем импорт нового исключения
import com.sarmo.chatservice.exception.PermissionDeniedException;
import com.sarmo.chatservice.dto.ChatMessageDTO;
import com.sarmo.chatservice.mapper.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(MessageRepository messageRepository, ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO, Long currentUserId) {
        logger.info("User {} sending message to chat {}: {}", currentUserId, chatMessageDTO.getChatId(), chatMessageDTO.getContent());

        chatService.getChatById(chatMessageDTO.getChatId(), currentUserId);

        if (!currentUserId.equals(chatMessageDTO.getSenderId())) {
            logger.warn("Message sender ID mismatch: current user {} tried to send as sender {}", currentUserId, chatMessageDTO.getSenderId());
            throw new PermissionDeniedException("Sender ID in message does not match authenticated user ID.");
        }

        Message message = MessageMapper.toMessage(chatMessageDTO);
        message.setTimestamp(LocalDateTime.now());
        message.setEdited(false);

        try {
            Message savedMessage = messageRepository.save(message);
            logger.info("Message saved with ID: {} for chat {}", savedMessage.getId(), savedMessage.getChatId());

            User sender = userService.getUserByIdEntity(savedMessage.getSenderId());
            ChatMessageDTO responseDTO = MessageMapper.toChatMessageDTO(savedMessage, sender);

            messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatId(), responseDTO);
            logger.debug("Message {} sent to WebSocket topic /topic/chat/{}", savedMessage.getId(), savedMessage.getChatId());

            return responseDTO;
        } catch (NoSuchElementException e) {
            logger.error("Sender user with ID {} not found for message in chat {}.", chatMessageDTO.getSenderId(), chatMessageDTO.getChatId());
            throw new IllegalArgumentException("Sender user not found: " + chatMessageDTO.getSenderId());
        } catch (Exception e) {
            logger.error("Error sending message to chat {}: {}", chatMessageDTO.getChatId(), e.getMessage(), e);
            throw new RuntimeException("Error sending message", e);
        }
    }

    @Transactional(readOnly = true)
    public ChatMessageDTO getMessageById(String messageId, Long currentUserId) {
        logger.info("User {} requesting message with ID: {}", currentUserId, messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    logger.warn("Message with ID {} not found.", messageId);
                    return new MessageNotFoundException("Message not found with ID: " + messageId);
                });

        // Проверка доступа: пользователь должен быть участником чата, к которому относится сообщение
        chatService.getChatById(message.getChatId(), currentUserId); // Этот метод уже проверяет членство

        try {
            User sender = userService.getUserByIdEntity(message.getSenderId());
            return MessageMapper.toChatMessageDTO(message, sender);
        } catch (NoSuchElementException e) {
            logger.warn("Sender user with ID {} not found for message {}. Returning DTO with unknown sender.", message.getSenderId(), message.getId());
            return MessageMapper.toChatMessageDTO(message, null);
        }
    }


    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessagesByChatId(Long chatId, Long currentUserId) {
        logger.info("User {} requesting messages for chat ID: {}", currentUserId, chatId);

        chatService.getChatById(chatId, currentUserId);

        List<Message> messages = messageRepository.findByChatIdOrderByTimestampAsc(chatId);
        logger.info("Found {} messages for chat ID: {}", messages.size(), chatId);

        return messages.stream()
                .map(msg -> {
                    try {
                        User sender = userService.getUserByIdEntity(msg.getSenderId());
                        return MessageMapper.toChatMessageDTO(msg, sender);
                    } catch (NoSuchElementException e) {
                        logger.warn("Sender user with ID {} not found for message {}. Returning DTO with unknown sender.", msg.getSenderId(), msg.getId());
                        return MessageMapper.toChatMessageDTO(msg, null);
                    }
                })
                .collect(Collectors.toList());
    }

    public ChatMessageDTO editMessage(String messageId, String newContent, Long currentUserId) {
        logger.info("User {} attempting to edit message ID {}. New content: {}", currentUserId, messageId, newContent);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    logger.warn("Message with ID {} not found for editing.", messageId);
                    return new MessageNotFoundException("Message not found with ID: " + messageId); // Используем MessageNotFoundException
                });

        if (!message.getSenderId().equals(currentUserId)) {
            logger.warn("User {} attempted to edit message {} but is not the sender {}", currentUserId, messageId, message.getSenderId());
            throw new PermissionDeniedException("You can only edit your own messages.");
        }

        chatService.getChatById(message.getChatId(), currentUserId);

        message.setContent(newContent);
        message.setEdited(true);
        message.setTimestamp(LocalDateTime.now());

        try {
            Message updatedMessage = messageRepository.save(message);
            logger.info("Message {} updated by user {}", messageId, currentUserId);

            User sender = userService.getUserByIdEntity(updatedMessage.getSenderId());
            ChatMessageDTO responseDTO = MessageMapper.toChatMessageDTO(updatedMessage, sender);

            messagingTemplate.convertAndSend("/topic/chat/" + updatedMessage.getChatId(), responseDTO);
            logger.debug("Edited message {} sent to WebSocket topic /topic/chat/{}", updatedMessage.getId(), updatedMessage.getChatId());

            return responseDTO;
        } catch (NoSuchElementException e) {
            logger.error("Sender user with ID {} not found during message edit for message {}.", message.getSenderId(), messageId);
            throw new IllegalArgumentException("Sender user not found: " + message.getSenderId());
        } catch (Exception e) {
            logger.error("Error editing message {}: {}", messageId, e.getMessage(), e);
            throw new RuntimeException("Error editing message", e);
        }
    }

    public void deleteMessage(String messageId, Long currentUserId) {
        logger.info("User {} attempting to delete message ID {}", currentUserId, messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    logger.warn("Message with ID {} not found for deletion.", messageId);
                    return new MessageNotFoundException("Message not found with ID: " + messageId); // Используем MessageNotFoundException
                });

        boolean isSender = message.getSenderId().equals(currentUserId);
        boolean isChatCreator = chatService.getChatByIdEntity(message.getChatId()).getCreator().getId().equals(currentUserId); // chatService.getChatByIdEntity должен быть public

        if (!isSender && !isChatCreator) {
            logger.warn("User {} attempted to delete message {} but is neither sender nor chat creator.", currentUserId, messageId);
            throw new PermissionDeniedException("You do not have permission to delete this message.");
        }

        try {
            messageRepository.delete(message);
            logger.info("Message {} deleted by user {}", messageId, currentUserId);

            messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), "{\"type\": \"MESSAGE_DELETED\", \"messageId\": \"" + messageId + "\"}");
            logger.debug("Message deletion notification for {} sent to WebSocket topic /topic/chat/{}", messageId, message.getChatId());

        } catch (Exception e) {
            logger.error("Error deleting message {}: {}", messageId, e.getMessage(), e);
            throw new RuntimeException("Error deleting message", e);
        }
    }
}