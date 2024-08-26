package com.example.order_app.controller;


import com.example.order_app.model.OrderProduct;
import com.example.order_app.repository.OrderProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-api/order-products")
public class OrderProductRestController {


    private final OrderProductRepository orderProductRepository;

    public OrderProductRestController(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return orderProductRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProduct> getOrderProductById(@PathVariable Long id) {
        return orderProductRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderProduct createOrderProduct(@RequestBody OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProduct> updateOrderProduct(@PathVariable Long id, @RequestBody OrderProduct orderProductDetails) {
        return orderProductRepository.findById(id)
                .map(orderProduct -> {
                    orderProduct.setQuantity(orderProductDetails.getQuantity());
                    return ResponseEntity.ok(orderProductRepository.save(orderProduct));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrderProduct(@PathVariable Long id) {
        return orderProductRepository.findById(id)
                .map(orderProduct -> {
                    orderProductRepository.delete(orderProduct);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}