package com.example.order_app.controller;

import com.example.order_app.model.Product;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;


    @GetMapping("/list")
    public String listProduct(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "Product/product_list";
    }
/*
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/product_add";
    }


    @PostMapping("/add")
    public String addProduct(Model model, Product product) {
        model.addAttribute("product", productService.save(product));
        return "redirect:/products/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/product_add";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products/list";
    }
*/
}
