package com.sarmo.listingservice.dto;

public class CreateRatingDto {
    private Integer value;

    public CreateRatingDto(){}

    public CreateRatingDto(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
