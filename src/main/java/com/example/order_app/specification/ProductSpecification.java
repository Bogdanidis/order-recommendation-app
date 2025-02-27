package com.example.order_app.specification;

import com.example.order_app.dto.SearchCriteria;
import com.example.order_app.model.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification implements Specification<Product> {
    private final SearchCriteria criteria;

    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String key = criteria.getKey();
        Object value = criteria.getValue();
        String operation = criteria.getOperation();

        // Handle nested properties (e.g., "category.name")
        if (key.contains(".")) {
            String[] parts = key.split("\\.");
            Path<Object> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }

            if (operation.equalsIgnoreCase(">")) {
                return builder.greaterThan(path.as(Comparable.class), (Comparable) value);
            } else if (operation.equalsIgnoreCase("<")) {
                return builder.lessThan(path.as(Comparable.class), (Comparable) value);
            } else if (operation.equalsIgnoreCase("=")) {
                return builder.equal(path, value);
            } else if (operation.equalsIgnoreCase(":")) {
                if (path.getJavaType() == String.class) {
                    return builder.like(
                            builder.lower(path.as(String.class)),
                            "%" + value.toString().toLowerCase() + "%");
                } else {
                    return builder.equal(path, value);
                }
            }
            return null;
        }

        // Regular (non-nested) property handling
        if (operation.equalsIgnoreCase(">")) {
            if (root.get(key).getJavaType() == BigDecimal.class) {
                return builder.greaterThan(root.get(key), (BigDecimal) value);
            } else {
                return builder.greaterThan(root.get(key), value.toString());
            }
        } else if (operation.equalsIgnoreCase("<")) {
            if (root.get(key).getJavaType() == BigDecimal.class) {
                return builder.lessThan(root.get(key), (BigDecimal) value);
            } else {
                return builder.lessThan(root.get(key), value.toString());
            }
        } else if (operation.equalsIgnoreCase("=")) {
            return builder.equal(root.get(key), value);
        } else if (operation.equalsIgnoreCase(":")) {
            if (root.get(key).getJavaType() == String.class) {
                return builder.like(
                        builder.lower(root.get(key)),
                        "%" + value.toString().toLowerCase() + "%");
            } else {
                return builder.equal(root.get(key), value);
            }
        }
        return null;
    }
}
