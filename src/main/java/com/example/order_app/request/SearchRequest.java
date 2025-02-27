package com.example.order_app.request;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request object for product search with filtering and sorting options.
 */
@Data
@NoArgsConstructor
public class SearchRequest {
    // Search filters
    private String name;
    private String brand;
    private String categoryName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minStock;
    private Boolean inStock;

    // Sorting options
    private String sortBy = "name";       // Default sort field
    private String sortDirection = "asc"; // Default sort direction

    /**
     * Returns whether any search criteria are specified
     */
    public boolean hasSearchCriteria() {
        return (name != null && !name.isEmpty()) ||
                (brand != null && !brand.isEmpty()) ||
                (categoryName != null && !categoryName.isEmpty()) ||
                minPrice != null ||
                maxPrice != null ||
                minStock != null ||
                Boolean.TRUE.equals(inStock);
    }
}