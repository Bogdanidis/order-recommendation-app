package com.example.order_app.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

//@Data
//@AllArgsConstructor
//public class RestResponse {
//    private String message;
//    private Object data;
//}


/**
 * Generic API response wrapper for all REST endpoints
 * @param <T> Type of data being returned
 */
@Data
@NoArgsConstructor
public class RestResponse<T> {
    private String message;
    private T data;
    private PageMetadata page;

    public RestResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public RestResponse(String message, Page<?> page) {
        this.message = message;
        this.data = (T) page.getContent();
        this.page = new PageMetadata(page);
    }
}

