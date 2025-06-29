package com.sarmo.subscriptionservice.dto;

public class CreatePlanFeatureDTO {
    private Long featureId;
    private String value;
    private String unit;

    public CreatePlanFeatureDTO() {
    }

    public CreatePlanFeatureDTO(Long featureId, String value, String unit) {
        this.featureId = featureId;
        this.value = value;
        this.unit = unit;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
