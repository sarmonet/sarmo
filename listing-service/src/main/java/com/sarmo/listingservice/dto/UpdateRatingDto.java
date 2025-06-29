package com.sarmo.listingservice.dto;

public class UpdateRatingDto {
    private Integer value;

    public UpdateRatingDto(){}

    public UpdateRatingDto(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
