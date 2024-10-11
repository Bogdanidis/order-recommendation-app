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
import org.springframework.beans.BeanUtils;
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

    /**
     * Displays a list of all products.
     *
     * @param model Spring MVC Model
     * @param page Page number
     * @param size Number of items per page
     * @return The name of the product list view
     */
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

    /**
     * Displays details of a specific product.
     *
     * @param productId ID of the product
     * @param model Spring MVC Model
     * @return The name of the product details view or a redirect URL
     */
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

    /**
     * Displays the form to add a new product.
     *
     * @param model Spring MVC Model
     * @return The name of the add product view
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("addProductRequest", new AddProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    /**
     * Handles the submission of a new product.
     *
     * @param product The product to be added
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
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
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/add";
        }
    }


    /**
     * Displays the form to update an existing product.
     *
     * @param productId ID of the product to update
     * @param model Spring MVC Model
     * @return The name of the update product view or a redirect URL
     */
    @GetMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showUpdateProductForm(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            // Map ProductDto to UpdateProductRequest
            BeanUtils.copyProperties(productDto, updateRequest);

            model.addAttribute("product", updateRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/update";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Handles the submission of an updated product.
     *
     * @param productId ID of the product to update
     * @param request The updated product data
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{productId}/update")
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
            Product theProduct = productService.updateProduct(request, productId);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Handles the deletion of a product.
     *
     * @param productId ID of the product to delete
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @DeleteMapping("/{productId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    /**
     * Handles product search functionality.
     *
     * @param brandName Brand name to search for
     * @param productName Product name to search for
     * @param category Category to search in
     * @param searched Whether a search has been performed
     * @param model Spring MVC Model
     * @return The name of the search results view
     */
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
