package com.example.order_app.response;

import lombok.Data;
import org.springframework.data.domain.Page;


 /**
 * Specialized response wrapper for search operations
 * @param <T> Type of data being returned
 */
@Data
public class SearchResponse<T> extends ApiResponse<T> {
    private String searchTerm;

    public SearchResponse() {
        super();
    }

    public SearchResponse(String message, Page<T> page, String searchTerm) {
        super(message, page);
        this.searchTerm = searchTerm;
    }
}