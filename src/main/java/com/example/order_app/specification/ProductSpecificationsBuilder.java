package com.example.order_app.specification;

import com.example.order_app.dto.SearchCriteria;
import com.example.order_app.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductSpecificationsBuilder {
    private final List<SearchCriteria> params;

    public ProductSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public ProductSpecificationsBuilder with(String key, String operation, Object value) {
        // Only add criteria if value is not null and not empty string
        if (value != null && !(value instanceof String && ((String)value).isEmpty())) {
            params.add(new SearchCriteria(key, operation, value));
        }
        return this;
    }

    public Specification<Product> build() {
        if (params.isEmpty()) {
            return null;
        }

        List<Specification<Product>> specs = params.stream()
                .map(ProductSpecification::new)
                .collect(Collectors.toList());

        Specification<Product> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }

        return result;
    }
}