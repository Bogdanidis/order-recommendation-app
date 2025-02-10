package com.example.order_app.controller.rest;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import com.example.order_app.request.UpdateCategoryRequest;
import com.example.order_app.response.RestResponse;
import com.example.order_app.response.SearchResponse;
import com.example.order_app.service.category.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryRestController {
    private final ICategoryService categoryService;

    /**
     * Search categories with optional name filter and pagination
     */
    @GetMapping("/search")
    @Operation(summary = "Search categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
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
    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<RestResponse<?>> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new RestResponse<>("Category found", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Add new category (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add new category", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    public ResponseEntity<RestResponse<?>> addCategory(
            @Valid @RequestBody AddCategoryRequest request) {
        try {
            Category category = categoryService.addCategory(request);
            return ResponseEntity.ok(new RestResponse<>(
                    "Category created successfully", category));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update category (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update category", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<RestResponse<?>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            Category category = categoryService.updateCategory(request, id);
            return ResponseEntity.ok(new RestResponse<>(
                    "Category updated successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }


    /**
     * Delete category (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete category", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<RestResponse<?>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(new RestResponse<>(
                    "Category deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}
