package com.sarmo.chatservice.dto;

import com.sarmo.chatservice.enums.AttachmentType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ChatMessageDTO {
    private String id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private boolean isEdited;
    private List<AttachmentDTO> attachments;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(String id, Long chatId, Long senderId, String senderName, String content, LocalDateTime timestamp, boolean isEdited, List<AttachmentDTO> attachments) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.isEdited = isEdited;
        this.attachments = attachments;
    }

    // Getters
    public String getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageDTO that = (ChatMessageDTO) o;
        return isEdited == that.isEdited && Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(senderId, that.senderId) && Objects.equals(senderName, that.senderName) && Objects.equals(content, that.content) && Objects.equals(timestamp, that.timestamp) && Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, senderId, senderName, content, timestamp, isEdited, attachments);
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id='" + id + '\'' +
                ", chatId=" + chatId +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isEdited=" + isEdited +
                ", attachments=" + attachments +
                '}';
    }

    // Builder pattern (optional, but good practice for DTOs)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Long chatId;
        private Long senderId;
        private String senderName;
        private String content;
        private LocalDateTime timestamp;
        private boolean isEdited;
        private List<AttachmentDTO> attachments;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder isEdited(boolean isEdited) {
            this.isEdited = isEdited;
            return this;
        }

        public Builder attachments(List<AttachmentDTO> attachments) {
            this.attachments = attachments;
            return this;
        }

        public ChatMessageDTO build() {
            return new ChatMessageDTO(id, chatId, senderId, senderName, content, timestamp, isEdited, attachments);
        }
    }
}