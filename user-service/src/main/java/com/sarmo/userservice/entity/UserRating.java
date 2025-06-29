package com.sarmo.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_ratings")
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID рейтинга

    @ManyToOne
    @JoinColumn(name = "rated_user_id") // Внешний ключ, ссылающийся на ID пользователя, которому поставили рейтинг
    private User ratedUser; // Пользователь, которому поставили рейтинг

    @Column(nullable = false)
    private Long userId; // ID пользователя, который поставил рейтинг

    @Column(nullable = false)
    private int value;

    public UserRating() {}

    public UserRating(Long id, User ratedUser, Long userId, int value) {
        this.id = id;
        this.ratedUser = ratedUser;
        this.userId = userId;
        this.value = value;
    }

    public UserRating(User ratedUser, Long userId, int value) {
        this.ratedUser = ratedUser;
        this.userId = userId;
        this.value = value;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}