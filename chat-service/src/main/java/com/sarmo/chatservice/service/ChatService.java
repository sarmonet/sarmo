package com.sarmo.chatservice.service;

import com.sarmo.chatservice.entity.Chat;
import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.enums.ChatType;
import com.sarmo.chatservice.repository.ChatRepository;
import com.sarmo.chatservice.exception.ChatNotFoundException;
import com.sarmo.chatservice.exception.PermissionDeniedException;
import com.sarmo.chatservice.service.UserService;
import com.sarmo.chatservice.mapper.ChatMapper;
import com.sarmo.chatservice.dto.ChatResponseDTO;
import com.sarmo.chatservice.dto.ChatCreationRequestDTO;
import com.sarmo.chatservice.dto.ChatUpdateRequestDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    @Transactional
    public ChatResponseDTO createChat(ChatCreationRequestDTO chatDTO, Long currentUserId) {
        logger.info("User {} is creating chat from DTO: {}", currentUserId, chatDTO);

        User creatorUser = userService.getUserByIdEntity(currentUserId);

        Chat chat = ChatMapper.toChat(chatDTO);

        Set<Long> userIds = Optional.ofNullable(chat.getUserIds()).orElseGet(HashSet::new);
        userIds.add(currentUserId);
        chat.setUserIds(userIds);

        chat.setCreator(creatorUser);

        if (chat.getType() == ChatType.PERSONAL) {
            logger.info("Processing personal chat creation for user {}", currentUserId);
            if (chat.getUserIds().size() != 2) {
                logger.warn("Personal chat creation failed for user {}: Invalid number of participants ({})", currentUserId, chat.getUserIds().size());
                throw new IllegalArgumentException("Personal chat must have exactly two participants (creator + one other)");
            }

            Long otherUserId = userIds.stream()
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("Personal chat creation failed for user {}: Could not find the other participant ID.", currentUserId);
                        return new IllegalStateException("Could not determine the other participant for personal chat");
                    });

            logger.info("Checking for existing personal chat between user {} and {}", currentUserId, otherUserId);
            Optional<Chat> existingPersonalChat = chatRepository.findByTypeAndUserIdsContainingAndUserIdsContaining(ChatType.PERSONAL, currentUserId, otherUserId);

            if (existingPersonalChat.isPresent()) {
                logger.info("Existing personal chat with ID {} found for users {} and {}. Returning existing chat.", existingPersonalChat.get().getId(), currentUserId, otherUserId);
                return ChatMapper.toChatResponseDTO(existingPersonalChat.get());
            }

            User otherUser = userService.getUserByIdEntity(otherUserId);

            String chatName = getPersonalChatName(otherUser, otherUserId);
            chat.setName(chatName);
            chat.setChatImageUrl(otherUser.getProfilePictureUrl());

            logger.info("Personal chat name set to: {}", chat.getName());

        } else if (chat.getType() == ChatType.GROUP) {
            logger.info("Processing group chat creation for user {}", currentUserId);
            if (chat.getName() == null || chat.getName().trim().isEmpty()) {
                logger.warn("Group chat creation failed for user {}: Group chat name is required.", currentUserId);
                throw new IllegalArgumentException("Group chat name is required for group chats");
            }
        } else {
            logger.warn("Chat creation failed for user {}: Unsupported chat type {}", currentUserId, chat.getType());
            throw new IllegalArgumentException("Unsupported chat type: " + chat.getType());
        }

        try {
            Chat createdChat = chatRepository.save(chat);
            logger.info("Chat created successfully with ID: {} by user {}", createdChat.getId(), currentUserId);
            return ChatMapper.toChatResponseDTO(createdChat);
        } catch (Exception e) {
            logger.error("Error saving chat by user {} from DTO: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Error creating chat", e);
        }
    }

    @Transactional(readOnly = true)
    public ChatResponseDTO getChatById(Long id, Long currentUserId) {
        logger.info("User {} is attempting to get chat by ID: {}", currentUserId, id);

        Chat chat = getChatByIdEntity(id);

        if (!chat.getUserIds().contains(currentUserId)) {
            logger.warn("Access to chat {} denied for user {}: Not a member", id, currentUserId);
            throw new PermissionDeniedException("You do not have permission to view this chat");
        }

        logger.info("Chat with ID {} found and access granted for user {}", id, currentUserId);

        ChatResponseDTO chatDTO = ChatMapper.toChatResponseDTO(chat);

        if (chat.getType() == ChatType.PERSONAL) {
            Long creatorId = Optional.ofNullable(chat.getCreator()).map(User::getId).orElse(null);

            if (creatorId != null && !currentUserId.equals(creatorId)) {
                logger.debug("Applying personal chat display logic: current user {} is not the creator {} for chat {}", currentUserId, creatorId, id);

                User creatorUser = userService.getUserByIdEntity(creatorId);
                String personalChatName = getPersonalChatName(creatorUser, creatorId);

                chatDTO.setName(personalChatName);
                chatDTO.setChatImageUrl(creatorUser.getProfilePictureUrl());

                logger.debug("DTO name set to '{}', image set to '{}'", chatDTO.getName(), chatDTO.getChatImageUrl());
            }
        }
        return chatDTO;
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDTO> getAllChats() {
        logger.info("Getting all chats");
        try {
            List<Chat> chats = chatRepository.findAll();
            logger.info("Successfully retrieved {} chats", chats.size());
            return ChatMapper.toChatResponseDTOs(chats);
        } catch (Exception e) {
            logger.error("Error getting all chats: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting all chats", e);
        }
    }

    @Transactional
    public ChatResponseDTO updateChat(Long chatId, ChatUpdateRequestDTO chatDetailsDTO, Long currentUserId) {
        logger.info("User {} is updating chat with ID {} from DTO", currentUserId, chatId);

        if (chatId == null) {
            throw new IllegalArgumentException("Chat ID cannot be null for update");
        }
        Chat existingChat = getChatByIdEntity(chatId);

        boolean isChatCreator = Optional.ofNullable(existingChat.getCreator())
                .map(User::getId)
                .filter(id -> id.equals(currentUserId))
                .isPresent();

        if (!isChatCreator) {
            logger.warn("User {} attempted to update chat {} without sufficient permissions (not creator)", currentUserId, chatId);
            throw new PermissionDeniedException("You do not have permission to update this chat");
        }

        ChatMapper.updateChatFromDto(chatDetailsDTO, existingChat);

        try {
            Chat updatedChat = chatRepository.save(existingChat);
            logger.info("Chat with ID {} updated by user {}", updatedChat.getId(), currentUserId);
            return ChatMapper.toChatResponseDTO(updatedChat);
        } catch (Exception e) {
            logger.error("Error updating chat {} by user {}: {}", chatId, currentUserId, e.getMessage(), e);
            throw new RuntimeException("Error updating chat", e);
        }
    }

    @Transactional
    public void deleteChat(Long id, Long currentUserId) {
        logger.info("User {} is deleting chat by ID: {}", currentUserId, id);

        Chat chatToDelete = getChatByIdEntity(id);

        boolean canDelete = false;
        if (chatToDelete.getType() == ChatType.PERSONAL){
            canDelete = true;
        } else if (chatToDelete.getType() == ChatType.GROUP) {
            canDelete = Optional.ofNullable(chatToDelete.getCreator())
                    .map(User::getId)
                    .filter(creatorId -> creatorId.equals(currentUserId))
                    .isPresent();
        }

        if (!canDelete) {
            logger.warn("User {} attempted to delete chat {} without sufficient permissions", currentUserId, id);
            throw new PermissionDeniedException("You do not have permission to delete this chat");
        }

        try {
            chatRepository.deleteById(id);
            logger.info("Chat with ID {} deleted by user {}", id, currentUserId);
        } catch (Exception e) {
            logger.error("Error deleting chat by ID {} by user {}: {}", id, currentUserId, e.getMessage(), e);
            throw new RuntimeException("Error deleting chat", e);
        }
    }

    @Transactional
    public ChatResponseDTO addUserToChat(Long chatId, Long userId, Long currentUserId) {
        logger.info("User {} is adding user {} to chat {}", currentUserId, userId, chatId);

        Chat chat = getChatByIdEntity(chatId);

        boolean isChatCreator = Optional.ofNullable(chat.getCreator())
                .map(User::getId)
                .filter(id -> id.equals(currentUserId))
                .isPresent();

        if (!isChatCreator) {
            logger.warn("User {} attempted to add user to chat {} without sufficient permissions (not creator)", currentUserId, chatId);
            throw new PermissionDeniedException("You do not have permission to add users to this chat");
        }

        if (chat.getType() == ChatType.PERSONAL) {
            logger.warn("Attempt to add user {} to personal chat {}. Personal chats cannot have more than 2 users.", userId, chatId);
            throw new IllegalArgumentException("Cannot add more users to a personal chat.");
        }

        User userToAdd = userService.getUserByIdEntity(userId);

        Set<Long> users = Optional.ofNullable(chat.getUserIds()).orElseGet(HashSet::new);

        if (!users.add(userId)) {
            logger.warn("User {} is already in chat {}", userId, chatId);
            return ChatMapper.toChatResponseDTO(chat);
        }
        chat.setUserIds(users);

        try {
            Chat updatedChat = chatRepository.save(chat);
            logger.info("User {} added to chat {} by user {}", userId, chatId, currentUserId);
            return ChatMapper.toChatResponseDTO(updatedChat);
        } catch (Exception e) {
            logger.error("Error adding user {} to chat {} by user {}: {}", userId, chatId, currentUserId, e.getMessage(), e);
            throw new RuntimeException("Error adding user to chat", e);
        }
    }

    @Transactional
    public ChatResponseDTO removeUserFromChat(Long chatId, Long userId, Long currentUserId) {
        logger.info("User {} is attempting to remove user {} from chat {}", currentUserId, userId, chatId);

        Chat chat = getChatByIdEntity(chatId);

        boolean isSelfRemoval = currentUserId.equals(userId);
        boolean isChatCreator = Optional.ofNullable(chat.getCreator())
                .map(User::getId)
                .filter(id -> id.equals(currentUserId))
                .isPresent();

        if (!isChatCreator && !isSelfRemoval) {
            logger.warn("User {} attempted to remove user {} from chat {} without sufficient permissions (not creator or self-removal)", currentUserId, userId, chatId);
            throw new PermissionDeniedException("You do not have permission to remove this user from this chat");
        }

        Set<Long> users = Optional.ofNullable(chat.getUserIds()).orElseGet(HashSet::new);

        if (!users.contains(userId)) {
            logger.warn("User {} is not in chat {}", userId, chatId);
            throw new NoSuchElementException("User to remove is not in this chat");
        }

        if (chat.getType() == ChatType.PERSONAL && users.size() == 1 && users.contains(userId)) {
            logger.warn("User {} attempted to remove the last member from personal chat {}. Personal chat cannot be empty.", currentUserId, chatId);
            throw new PermissionDeniedException("Cannot remove the last member from a personal chat.");
        }

        if (!users.remove(userId)) {
            logger.warn("Failed to remove user {} from chat {}. User not found in set.", userId, chatId);
            throw new IllegalStateException("Failed to remove user from chat. User not found in set after check.");
        }
        chat.setUserIds(users);

        try {
            Chat updatedChat = chatRepository.save(chat);
            logger.info("User {} removed from chat {} by user {}", userId, chatId, currentUserId);

            if (updatedChat.getUserIds().isEmpty()) {
                logger.info("Chat {} became empty after user {} was removed. Deleting chat.", chatId, userId);
                chatRepository.deleteById(chatId);
                return null;
            }
            return ChatMapper.toChatResponseDTO(updatedChat);
        } catch (Exception e) {
            logger.error("Error removing user {} from chat {} by user {}: {}", userId, chatId, currentUserId, e.getMessage(), e);
            throw new RuntimeException("Error removing user from chat", e);
        }
    }

    public Chat getChatByIdEntity(Long id) {
        logger.debug("Fetching chat entity by ID: {}", id);
        return chatRepository.findById(id).orElseThrow(() -> {
            logger.warn("Chat entity with ID {} not found", id);
            return new ChatNotFoundException("Chat not found with ID: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDTO> getChatsByUserId(Long userId) {
        logger.info("Getting chats for user ID: {}", userId);
        try {
            List<Chat> chats = chatRepository.findByUserIdsContaining(userId);
            logger.info("Found {} chats for user ID: {}", chats.size(), userId);

            List<ChatResponseDTO> dtos = new java.util.ArrayList<>();

            for (Chat chat : chats) {
                ChatResponseDTO dto = ChatMapper.toChatResponseDTO(chat);

                if (chat.getType() == ChatType.PERSONAL) {
                    Long otherParticipantId = chat.getUserIds().stream()
                            .filter(id -> !id.equals(userId))
                            .findFirst()
                            .orElse(null);

                    if (otherParticipantId != null) {
                        logger.debug("Applying personal chat display logic in list: current user {} is viewing chat with other participant {}", userId, otherParticipantId);
                        User otherUser = userService.getUserByIdEntity(otherParticipantId);
                        String personalChatName = getPersonalChatName(otherUser, otherParticipantId);
                        dto.setName(personalChatName);
                        dto.setChatImageUrl(otherUser.getProfilePictureUrl());
                        logger.debug("DTO name set to '{}', image set to '{}' for chat {}", dto.getName(), dto.getChatImageUrl(), chat.getId());
                    } else {
                        logger.warn("Personal chat {} for user {} has no other participant. Displaying as self-chat or broken.", chat.getId(), userId);
                        dto.setName("Self Chat (Error)");
                        dto.setChatImageUrl(null);
                    }
                }
                dtos.add(dto);
            }
            return dtos;

        } catch (Exception e) {
            logger.error("Error getting chats for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error getting chats for user", e);
        }
    }

    private String getPersonalChatName(User user, Long userId) {
        String firstName = Optional.ofNullable(user.getFirstName()).orElse("");
        String lastName = Optional.ofNullable(user.getLastName()).orElse("");
        String chatName = (firstName + " " + lastName).trim();
        return chatName.isEmpty() ? "Chat with User ID: " + userId : chatName;
    }
}