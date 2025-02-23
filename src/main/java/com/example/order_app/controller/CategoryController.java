package com.example.order_app.controller;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import com.example.order_app.request.UpdateCategoryRequest;
import com.example.order_app.service.category.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final ICategoryService categoryService;


    /**
     * Displays a paginated list of all categories with optional search functionality.
     *
     * @param page Page number (default: 0)
     * @param size Number of items per page (default: 10)
     * @param name Optional category name to filter categories
     * @param model Spring MVC Model
     * @return The name of the category list view
     */
    @GetMapping
    public String getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            Model model) {
        Page<Category> categoryPage = categoryService.searchCategories(PageRequest.of(page, size), name);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("name", name);
        return "category/list";
    }
    /**
     * Displays details of a specific category.
     *
     * @param id ID of the category
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return The name of the category details view or a redirect URL
     */
    @GetMapping("/{id}")
    public String getCategoryById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category theCategory = categoryService.getCategoryById(id);
            model.addAttribute("category", theCategory);
            return "category/details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories";
        }
    }

    /**
     * Displays the form to add a new category.
     *
     * @param model Spring MVC Model
     * @return The name of the add category view
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("addCategoryRequest", new AddCategoryRequest());
        return "category/add";
    }

    /**
     * Handles the submission of a new category.
     *
     * @param category The category to be added
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCategory(@Valid @ModelAttribute("category") AddCategoryRequest category,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "category/add";
        }
        try {
            Category addedCategory = categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category added successfully");
            return "redirect:/categories/" + addedCategory.getId();
        } catch (AlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "category/add";
        }
    }

    /**
     * Displays the form to update an existing category.
     *
     * @param id ID of the category to update
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return The name of the update category view or a redirect URL
     */
    @GetMapping("/{id}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showUpdateCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(id);
            UpdateCategoryRequest updateRequest = new UpdateCategoryRequest();
            updateRequest.setId(category.getId());
            updateRequest.setName(category.getName());
            updateRequest.setDescription(category.getDescription());
            model.addAttribute("category", updateRequest);
            return "category/update";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories";
        }
    }


    /**
     * Handles the submission of an updated category.
     *
     * @param id ID of the category to update
     * @param category The updated category data
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") UpdateCategoryRequest category,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "category/update";
        }
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
            return "redirect:/categories/" + updatedCategory.getId();
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "category/update";
        }
    }

    /**
     * Handles the deletion of a category.
     *
     * @param id ID of the category to delete
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("success", "Category deactivated  successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }

}