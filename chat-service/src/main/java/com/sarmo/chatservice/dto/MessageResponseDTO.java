package com.sarmo.chatservice.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MessageResponseDTO {
    private String id;
    private Long chatId;
    private Long senderId; // Можно заменить на UserResponseDTO sender; если нужно возвращать полные данные отправителя
    private String content;
    private LocalDateTime timestamp;
    private boolean isEdited;
    private LocalDateTime editedTimestamp; // Время последнего редактирования
    private Set<Long> readBy = new HashSet<>(); // ID прочитавших пользователей
    private List<AttachmentDTO> attachments = new ArrayList<>();

    public MessageResponseDTO() {}

    public MessageResponseDTO(String id, Long chatId, Long senderId, String content, LocalDateTime timestamp, boolean isEdited, LocalDateTime editedTimestamp, Set<Long> readBy, List<AttachmentDTO> attachments) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.isEdited = isEdited;
        this.editedTimestamp = editedTimestamp;
        this.readBy = readBy;
        this.attachments = attachments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public LocalDateTime getEditedTimestamp() {
        return editedTimestamp;
    }

    public void setEditedTimestamp(LocalDateTime editedTimestamp) {
        this.editedTimestamp = editedTimestamp;
    }

    public Set<Long> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<Long> readBy) {
        this.readBy = readBy;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageResponseDTO that = (MessageResponseDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MessageResponseDTO{" +
                "id='" + id + '\'' +
                ", chatId=" + chatId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isEdited=" + isEdited +
                ", editedTimestamp=" + editedTimestamp +
                ", readBy=" + readBy +
                ", attachments=" + attachments +
                '}';
    }
}