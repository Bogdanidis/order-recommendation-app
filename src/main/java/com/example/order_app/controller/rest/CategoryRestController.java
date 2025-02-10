package com.example.order_app.controller.rest;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import com.example.order_app.request.UpdateCategoryRequest;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.response.SearchResponse;
import com.example.order_app.service.category.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryRestController {
    private final ICategoryService categoryService;

    /**
     * Search categories with optional name filter and pagination
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse<?>> searchCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {

        Page<Category> categoryPage = categoryService.searchCategories(
                PageRequest.of(page, size), name);

        return ResponseEntity.ok(new SearchResponse<>(
                "Categories found", categoryPage, name));
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>("Category found", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Add new category (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> addCategory(
            @Valid @RequestBody AddCategoryRequest request) {
        try {
            Category category = categoryService.addCategory(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Category created successfully", category));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update category (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            Category category = categoryService.updateCategory(request, id);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Category updated successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }


    /**
     * Delete category (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Category deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}
