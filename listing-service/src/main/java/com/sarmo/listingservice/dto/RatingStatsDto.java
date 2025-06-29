package com.sarmo.listingservice.dto;

public class RatingStatsDto {

    private Double averageRating;
    private Long totalVotes;

    public RatingStatsDto() {}

    public RatingStatsDto(Double averageRating, Long totalVotes) {
        this.averageRating = averageRating;
        this.totalVotes = totalVotes;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Long totalVotes) {
        this.totalVotes = totalVotes;
    }
}