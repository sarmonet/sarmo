package com.sarmo.listingservice.entity;

public class Field {
    private String name; // Название поля
    private String type; // Тип данных (String, Integer, Array, etc.)
    private Boolean isRequired; // Обязательность поля
    private Boolean filterable;


    public Field() {
    }

    public Field(String name, String type, Boolean isRequired, Boolean filterable) {
        this.name = name;
        this.type = type;
        this.isRequired = isRequired;
        this.filterable = filterable;
    }

    public Boolean getFilterable() {
        return filterable;
    }

    public void setFilterable(Boolean filterable) {
        this.filterable = filterable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }
}