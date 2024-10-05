//package com.example.order_app.controller;
//
//import com.example.order_app.model.User;
//import com.example.order_app.service.OrderService;
//import com.example.order_app.service.product.ProductService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@AllArgsConstructor
//@RequestMapping("orders")
//public class OrderController {
//
//    private final OrderService orderService;
//    private final ProductService productService;
//
//    // Handle ordering of products
//    @PostMapping("/make")
//    public String orderProducts(@RequestParam("productId") Long productId,
//                                @RequestParam("quantity") Integer quantity, Authentication authentication) {
//        // Implement logic to add the product to the user's order
//        // Redirect to a confirmation page or back to the products page
//        User user = (User) authentication.getPrincipal();
//        productService.orderProduct(productId, quantity, user.getId());
//        return "redirect:/customer/browse-products?orderSuccess=true";
//    }
//
//    @PostMapping("/cancel/{orderId}")
//    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
//        try {
//            orderService.cancelOrder(orderId);
//            return ResponseEntity.ok("Order cancelled successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error cancelling order: " + e.getMessage());
//        }
//    }
//}
