package com.example.order_app.controller;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.model.Product;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final ICategoryService categoryService;

    @GetMapping
    public String getAllProducts(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "9") int size) {
        Page<Product> productPage = productService.getAllProducts(PageRequest.of(page, size));
        List<ProductDto> convertedProducts = productService.getConvertedProducts(productPage.getContent());

        model.addAttribute("products", convertedProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        return "product/list";
    }

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            model.addAttribute("product", productDto);
            return "product/details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }


    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new AddProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addProduct(@Valid @ModelAttribute("product") AddProductRequest product,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
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
            ProductDto productDto = productService.convertToDto(product);
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            // Map ProductDto to UpdateProductRequest
            updateRequest.setId(productDto.getId());
            updateRequest.setName(productDto.getName());
            updateRequest.setDescription(productDto.getDescription());
            updateRequest.setStock(productDto.getStock());
            updateRequest.setPrice(productDto.getPrice());
            updateRequest.setBrand(productDto.getBrand());
            updateRequest.setCategory(productDto.getCategory());

            model.addAttribute("product", updateRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/update";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateProduct(@PathVariable Long productId,
                                @Valid @ModelAttribute("product") UpdateProductRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/update";
        }
        try {
            //Category category = categoryService.getCategoryById(categoryId);
            //request.setCategory(category);
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
    public String searchProducts(
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean searched,
            Model model) {

        if (Boolean.TRUE.equals(searched)) {
            List<ProductDto> products = productService.searchProducts(brandName, productName, category);
            model.addAttribute("products", products);
            model.addAttribute("searched", true);
        }

        model.addAttribute("brandName", brandName);
        model.addAttribute("productName", productName);
        model.addAttribute("category", category);
        return "product/search";
    }

}
