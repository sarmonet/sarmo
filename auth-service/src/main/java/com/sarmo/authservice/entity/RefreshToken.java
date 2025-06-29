package com.sarmo.authservice.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private Instant creationDate; // Добавлено поле

    // Конструктор без аргументов (необходим для JPA)
    public RefreshToken() {}

    // Приватный конструктор для билдера
    private RefreshToken(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.token = builder.token;
        this.expiryDate = builder.expiryDate;
        this.creationDate = builder.creationDate; // Добавлено
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    // equals и hashCode (для сравнения объектов)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(token, that.token) && Objects.equals(expiryDate, that.expiryDate) && Objects.equals(creationDate, that.creationDate); // Добавлено
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, token, expiryDate, creationDate); // Добавлено
    }

    // toString (для представления объекта в виде строки)
    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                ", creationDate=" + creationDate + // Добавлено
                '}';
    }

    // Билдер
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private String token;
        private Instant expiryDate;
        private Instant creationDate; // Добавлено

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder creationDate(Instant creationDate) { // Добавлено
            this.creationDate = creationDate;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this);
        }
    }
}