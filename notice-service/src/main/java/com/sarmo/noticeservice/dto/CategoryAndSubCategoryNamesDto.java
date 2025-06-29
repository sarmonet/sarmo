package com.sarmo.noticeservice.dto;

public class CategoryAndSubCategoryNamesDto {
    private String categoryName;
    private String subCategoryName;

    public CategoryAndSubCategoryNamesDto() {
    }

    public CategoryAndSubCategoryNamesDto(String categoryName, String subCategoryName) {
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}