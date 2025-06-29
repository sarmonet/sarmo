package com.sarmo.listingservice.dto;

import java.time.LocalDateTime;

public class CountNewListingsRequestDto {
    private LocalDateTime from;
    private LocalDateTime to;
    private Object filters; // Теперь принимаем Object для произвольного JSON

    public CountNewListingsRequestDto() {
    }

    public CountNewListingsRequestDto(LocalDateTime from, LocalDateTime to, Object filters) {
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

    public Object getFilters() {
        return filters;
    }

    public void setFilters(Object filters) {
        this.filters = filters;
    }
}