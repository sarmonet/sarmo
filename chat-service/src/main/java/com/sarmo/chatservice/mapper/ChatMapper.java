package com.sarmo.chatservice.mapper;

import com.sarmo.chatservice.entity.Chat;
import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.dto.ChatResponseDTO;
import com.sarmo.chatservice.dto.ChatCreationRequestDTO;
import com.sarmo.chatservice.dto.ChatUpdateRequestDTO;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMapper {

    public static Chat toChat(ChatCreationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Chat chat = new Chat();
        chat.setName(dto.getName());
        chat.setType(dto.getType());
        chat.setChatImageUrl(dto.getChatImageUrl());
        if (dto.getParticipantIds() != null) {
            chat.setUserIds(new HashSet<>(dto.getParticipantIds()));
        } else {
            chat.setUserIds(new HashSet<>());
        }
        return chat;
    }

    public static void updateChatFromDto(ChatUpdateRequestDTO dto, Chat chat) {
        if (dto == null || chat == null) {
            return;
        }
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            chat.setName(dto.getName().trim());
        }
        if (dto.getType() != null) {
            chat.setType(dto.getType());
        }
    }

    public static ChatResponseDTO toChatResponseDTO(Chat chat) {
        if (chat == null) {
            return null;
        }

        ChatResponseDTO dto = new ChatResponseDTO();

        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setChatImageUrl(chat.getChatImageUrl());
        dto.setType(chat.getType());
        dto.setCreationDate(chat.getCreationDate());

        User creatorUser = chat.getCreator();
        if (creatorUser != null) {
            dto.setCreatorId(creatorUser.getId());

        }

        if (chat.getUserIds() != null) {
            dto.setUserIds(new HashSet<>(chat.getUserIds()));
        } else {
            dto.setUserIds(new HashSet<>());
        }

        return dto;
    }

    public static List<ChatResponseDTO> toChatResponseDTOs(List<Chat> chats) {
        if (chats == null) {
            return null;
        }
        return chats.stream()
                .map(ChatMapper::toChatResponseDTO)
                .collect(Collectors.toList());
    }
}