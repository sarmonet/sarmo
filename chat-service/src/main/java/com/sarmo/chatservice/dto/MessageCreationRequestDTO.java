package com.sarmo.chatservice.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageCreationRequestDTO {
    private Long chatId;
    private String content;
    private List<AttachmentDTO> attachments = new ArrayList<>();

    public MessageCreationRequestDTO() {}

    public MessageCreationRequestDTO(Long chatId, String content, List<AttachmentDTO> attachments) {
        this.chatId = chatId;
        this.content = content;
        if (attachments != null) {
            this.attachments.addAll(attachments);
        }
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        MessageCreationRequestDTO that = (MessageCreationRequestDTO) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(content, that.content) && Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, content, attachments);
    }

    @Override
    public String toString() {
        return "MessageCreationRequestDTO{" +
                "chatId=" + chatId +
                ", content='" + content + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}