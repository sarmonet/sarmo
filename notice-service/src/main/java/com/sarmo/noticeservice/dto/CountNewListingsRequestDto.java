package com.sarmo.noticeservice.dto;

import java.time.LocalDateTime;

public class CountNewListingsRequestDto {
    private LocalDateTime from;
    private LocalDateTime to;
    private String filters;

    public CountNewListingsRequestDto() {
    }

    public CountNewListingsRequestDto(LocalDateTime from, LocalDateTime to, String filters) {
        this.from = from;
        this.to = to;
        this.filters = filters;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }
}