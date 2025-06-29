package com.sarmo.chatservice.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageUpdateRequestDTO {
    // ID сообщения будет в пути запроса, а не в теле DTO
    private String content;
    private List<AttachmentDTO> attachments = new ArrayList<>(); // Позволяет заменить вложения

    public MessageUpdateRequestDTO() {}

    public MessageUpdateRequestDTO(String content, List<AttachmentDTO> attachments) {
        this.content = content;
        if (attachments != null) {
            this.attachments.addAll(attachments);
        }
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
        MessageUpdateRequestDTO that = (MessageUpdateRequestDTO) o;
        return Objects.equals(content, that.content) && Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, attachments);
    }

    @Override
    public String toString() {
        return "MessageUpdateRequestDTO{" +
                "content='" + content + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}