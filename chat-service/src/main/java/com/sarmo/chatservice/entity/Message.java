package com.sarmo.chatservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private Long chatId;
    private Long senderId;
    private String content;

    private LocalDateTime timestamp;

    private boolean isEdited;
    private List<Attachment> attachments = new ArrayList<>();

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(Long chatId, Long senderId, String content, List<Attachment> attachments) {
        this();
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        if (attachments != null) {
            this.attachments.addAll(attachments);
        }
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id != null && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    // --- Переопределение toString() ---
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", chatId=" + chatId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isEdited=" + isEdited +
                ", attachments=" + attachments +
                '}';
    }
}