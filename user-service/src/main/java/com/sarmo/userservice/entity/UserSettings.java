package com.sarmo.userservice.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_settings")
public class UserSettings {

    @Id
    private String id;

    private Object settings; // Объект с настройками (например, JSON)

    public UserSettings() {
    }

    public UserSettings(String id, Object settings) {
        this.id = id;
        this.settings = settings;
    }

    public Object getSettings() {
        return settings;
    }

    public void setSettings(Object settings) {
        this.settings = settings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}