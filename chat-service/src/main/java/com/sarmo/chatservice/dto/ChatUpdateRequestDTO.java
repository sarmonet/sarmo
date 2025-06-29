package com.sarmo.chatservice.dto;

import com.sarmo.chatservice.enums.ChatType;
import java.util.Objects;

public class ChatUpdateRequestDTO {
    private String name;
    private ChatType type;

    public ChatUpdateRequestDTO() {}

    public ChatUpdateRequestDTO(String name, ChatType type) {
        this.name = name;
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUpdateRequestDTO that = (ChatUpdateRequestDTO) o;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "ChatUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}