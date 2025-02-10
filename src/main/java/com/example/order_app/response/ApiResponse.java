package com.example.order_app.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

//@Data
//@AllArgsConstructor
//public class ApiResponse {
//    private String message;
//    private Object data;
//}


/**
 * Generic API response wrapper for all REST endpoints
 * @param <T> Type of data being returned
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    private PageMetadata page;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, Page<?> page) {
        this.message = message;
        this.data = (T) page.getContent();
        this.page = new PageMetadata(page);
    }
}

