package com.example.order_app.exception;

public class OrderCancellationException extends BusinessException {
    public OrderCancellationException(String message) {
        super(message);
    }
}