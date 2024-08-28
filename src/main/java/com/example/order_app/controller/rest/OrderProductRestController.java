package com.example.order_app.controller.rest;


import com.example.order_app.model.OrderProduct;
import com.example.order_app.repository.OrderProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-api/order-products")
@Tag(name = "Order Products", description = "Endpoints for managing order products")
public class OrderProductRestController {


    private final OrderProductRepository orderProductRepository;

    public OrderProductRestController(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @GetMapping
    @Operation(summary = "Get all order products", description = "Fetch a list of all order products")
    public List<OrderProduct> getAllOrderProducts() {
        return orderProductRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order product by ID", description = "Fetch details of a specific order product by its ID")
    public ResponseEntity<OrderProduct> getOrderProductById(@PathVariable Long id) {
        return orderProductRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new order product", description = "Create a new order product with the provided details")
    public OrderProduct createOrderProduct(@RequestBody OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order product", description = "Update an existing order product by its ID")
    public ResponseEntity<OrderProduct> updateOrderProduct(@PathVariable Long id, @RequestBody OrderProduct orderProductDetails) {
        return orderProductRepository.findById(id)
                .map(orderProduct -> {
                    orderProduct.setQuantity(orderProductDetails.getQuantity());
                    return ResponseEntity.ok(orderProductRepository.save(orderProduct));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order product", description = "Delete an order product by its ID")
    public ResponseEntity<Object> deleteOrderProduct(@PathVariable Long id) {
        return orderProductRepository.findById(id)
                .map(orderProduct -> {
                    orderProductRepository.delete(orderProduct);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}