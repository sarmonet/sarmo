// src/main/java/com/sarmo/chatservice/dto/ChatCreationRequestDTO.java
package com.sarmo.chatservice.dto;

import com.sarmo.chatservice.enums.ChatType;
import java.util.HashSet; // Добавьте импорт
import java.util.Objects;
import java.util.Set;

public class ChatCreationRequestDTO {
    private String name;
    private String chatImageUrl;
    private ChatType type;
    private Set<Long> participantIds = new HashSet<>();

    public ChatCreationRequestDTO() {}

    public ChatCreationRequestDTO(String name, String chatImageUrl, ChatType type, Set<Long> participantIds) {
        this.name = name;
        this.chatImageUrl = chatImageUrl;
        this.type = type;
        this.participantIds = participantIds;
    }

    public String getChatImageUrl() {
        return chatImageUrl;
    }
    public void setChatImageUrl(String chatImageUrl) {
        this.chatImageUrl = chatImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    // Геттер и Сеттер для participantIds
    public Set<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(Set<Long> participantIds) {
        this.participantIds = participantIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatCreationRequestDTO that = (ChatCreationRequestDTO) o;
        return Objects.equals(name, that.name) && type == that.type && Objects.equals(participantIds, that.participantIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, participantIds);
    }

    @Override
    public String toString() {
        return "ChatCreationRequestDTO{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", participantIds=" + participantIds +
                '}';
    }
}