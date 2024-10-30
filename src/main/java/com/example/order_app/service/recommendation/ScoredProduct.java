package com.example.order_app.service.recommendation;

import com.example.order_app.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoredProduct implements Comparable<ScoredProduct> {
    private Product product;
    private double score;

    @Override
    public int compareTo(ScoredProduct other) {
        return Double.compare(other.score, this.score); // Descending order
    }
}