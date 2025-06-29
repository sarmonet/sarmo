package com.sarmo.kafka.dto;

public class UserImageData {
    private Long userId;

    private String userImageUrl;

    public UserImageData() {}

    public UserImageData(Long userId, String userImageUrl) {
        this.userId = userId;
        this.userImageUrl = userImageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
