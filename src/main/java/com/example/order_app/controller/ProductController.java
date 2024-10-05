package com.example.order_app.controller;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        model.addAttribute("products", convertedProducts);
        return "products/list";
    }

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            model.addAttribute("product", productDto);
            return "products/details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new AddProductRequest());
        return "products/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addProduct(@ModelAttribute AddProductRequest product, RedirectAttributes redirectAttributes) {
        try {
            Product theProduct = productService.addProduct(product);
            redirectAttributes.addFlashAttribute("message", "Product added successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/add";
        }
    }

    @GetMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showUpdateProductForm(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            model.addAttribute("product", product);
            return "products/update";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            //return "error/404";
            return "redirect:/products";
        }
    }

    @PostMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateProduct(@PathVariable Long productId, @ModelAttribute UpdateProductRequest request, RedirectAttributes redirectAttributes) {
        try {
            Product theProduct = productService.updateProduct(request, productId);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/{productId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String brandName,
                                 @RequestParam(required = false) String productName,
                                 @RequestParam(required = false) String category,
                                 Model model) {
        List<ProductDto> products = new ArrayList<>();

        if (brandName != null && productName != null) {
            products = productService.getConvertedProducts(productService.getProductsByBrandAndName(brandName, productName));
        } else if (category != null && brandName != null) {
            products = productService.getConvertedProducts(productService.getProductsByCategoryAndBrand(category, brandName));
        } else if (productName != null) {
            products = productService.getConvertedProducts(productService.getProductsByName(productName));
        } else if (brandName != null) {
            products = productService.getConvertedProducts(productService.getProductsByBrand(brandName));
        } else if (category != null) {
            products = productService.getConvertedProducts(productService.getProductsByCategory(category));
        }

        model.addAttribute("products", products);
        model.addAttribute("brandName", brandName);
        model.addAttribute("productName", productName);
        model.addAttribute("category", category);
        return "products/search";
    }

    @GetMapping("/count")
    public String countProducts(@RequestParam String brand, @RequestParam String name, Model model) {
        try {
            long productCount = productService.countProductsByBrandAndName(brand, name);
            model.addAttribute("productCount", productCount);
            model.addAttribute("brand", brand);
            model.addAttribute("name", name);
            return "products/count";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }
}
