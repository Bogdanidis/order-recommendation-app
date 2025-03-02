package com.example.order_app.exception;

import lombok.Getter;

@Getter
public class OutOfStockException extends BusinessException {
    private final Long productId;
    private final int requestedQuantity;
    private final int availableStock;

    public OutOfStockException(Long productId, int requestedQuantity, int availableStock) {
        super(String.format("Product with ID %d is out of stock. Requested: %d, Available: %d",
                productId, requestedQuantity, availableStock));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

}
