package com.sarmo.chatservice.dto;

import com.sarmo.chatservice.enums.ChatType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatResponseDTO {
    private Long id;
    private String name;
    private String chatImageUrl;
    private ChatType type;
    private LocalDateTime creationDate;
    private Long creatorId;
    private Set<Long> userIds = new HashSet<>();

    public ChatResponseDTO() {}


    public String getChatImageUrl() {
        return chatImageUrl;
    }

    public void setChatImageUrl(String chatImageUrl) {
        this.chatImageUrl = chatImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    // Геттер и Сеттер для userIds
    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponseDTO that = (ChatResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && type == that.type && Objects.equals(creationDate, that.creationDate) && Objects.equals(creatorId, that.creatorId) && Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, creationDate, creatorId, userIds);
    }

    @Override
    public String toString() {
        return "ChatResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", creationDate=" + creationDate +
                ", creatorId=" + creatorId +
                ", userIds=" + userIds + '\'' +
                '}';
    }
}