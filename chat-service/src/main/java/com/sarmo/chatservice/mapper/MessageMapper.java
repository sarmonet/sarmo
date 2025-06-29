package com.sarmo.chatservice.mapper;

import com.sarmo.chatservice.dto.AttachmentDTO;
import com.sarmo.chatservice.dto.ChatMessageDTO;
import com.sarmo.chatservice.entity.Attachment;
import com.sarmo.chatservice.entity.Message;
import com.sarmo.chatservice.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageMapper {

    private MessageMapper() {
    }

    public static ChatMessageDTO toChatMessageDTO(Message message, User sender) {
        if (message == null) {
            return null;
        }

        String senderName = Optional.ofNullable(sender)
                .map(u -> {
                    String firstName = Optional.ofNullable(u.getFirstName()).orElse("");
                    String lastName = Optional.ofNullable(u.getLastName()).orElse("");
                    String fullName = (firstName + " " + lastName).trim();
                    return fullName.isEmpty() ? "User " + u.getId() : fullName;
                })
                .orElse("Unknown User");


        List<AttachmentDTO> attachmentDTOs = Optional.ofNullable(message.getAttachments())
                .orElse(Collections.emptyList())
                .stream()
                .map(MessageMapper::toAttachmentDTO)
                .collect(Collectors.toList());

        return ChatMessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .isEdited(message.isEdited())
                .attachments(attachmentDTOs)
                .build();
    }

    public static Message toMessage(ChatMessageDTO chatMessageDTO) {
        if (chatMessageDTO == null) {
            return null;
        }
        Message message = new Message();
        message.setId(chatMessageDTO.getId());
        message.setChatId(chatMessageDTO.getChatId());
        message.setSenderId(chatMessageDTO.getSenderId());
        message.setContent(chatMessageDTO.getContent());
        message.setTimestamp(chatMessageDTO.getTimestamp());
        message.setEdited(chatMessageDTO.isEdited());
        message.setAttachments(Optional.ofNullable(chatMessageDTO.getAttachments())
                .orElse(Collections.emptyList())
                .stream()
                .map(MessageMapper::toAttachment)
                .collect(Collectors.toList()));
        return message;
    }

    public static AttachmentDTO toAttachmentDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return AttachmentDTO.builder()
                .url(attachment.getUrl())
                .type(attachment.getType())
                .build();
    }

    public static Attachment toAttachment(AttachmentDTO attachmentDTO) {
        if (attachmentDTO == null) {
            return null;
        }
        return new Attachment(attachmentDTO.getUrl(), attachmentDTO.getType());
    }
}