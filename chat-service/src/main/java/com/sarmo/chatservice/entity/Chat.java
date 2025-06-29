package com.sarmo.chatservice.entity;

import com.sarmo.chatservice.enums.ChatType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatImageUrl;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChatType type;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_users", joinColumns = @JoinColumn(name = "chat_id"))
    @Column(name = "user_id")
    private Set<Long> userIds = new HashSet<>();


    public Chat() {}

    public Chat(String chatImageUrl, String name, ChatType type, LocalDateTime creationDate, User creator, Set<Long> userIds) {
        this.chatImageUrl = chatImageUrl;
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.creator = creator;
        this.userIds = userIds;
    }

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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

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
        Chat chat = (Chat) o;
        return id != null && Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", creationDate=" + creationDate +
                ", creatorId=" + (creator != null ? creator.getId() : "null") + // В toString отображаем ID создателя
                ", userIds=" + userIds + '\'' +
                '}';
    }
}