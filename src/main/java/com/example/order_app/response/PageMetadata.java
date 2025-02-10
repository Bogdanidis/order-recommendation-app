package com.example.order_app.response;

import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * Metadata class for pagination information
 */
@Data
public class PageMetadata {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public PageMetadata(Page<?> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}
