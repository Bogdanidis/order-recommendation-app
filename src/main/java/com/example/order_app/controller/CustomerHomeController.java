package com.example.order_app.controller;

import com.example.order_app.model.Product;
import com.example.order_app.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("customer")

public class CustomerHomeController {

    private final ProductService productService;

    public CustomerHomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/home")
    public String userHome() {
        return "customer_home";
    }

    // Display all products
    @GetMapping("/browse-products")
    public String browseProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "Product/browse_products";
    }

    // Handle ordering of products
    @PostMapping("/order")
    public String orderProducts(@RequestParam("productId") Long productId,
                                @RequestParam("quantity") Integer quantity) {
        // Implement logic to add the product to the user's order
        // Redirect to a confirmation page or back to the products page
        productService.orderProduct(productId, quantity);
        return "redirect:/customer/browse-products?orderSuccess=true";
    }

}
