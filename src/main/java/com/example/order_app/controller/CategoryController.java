package com.example.order_app.controller;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final ICategoryService categoryService;


    /**
     * Displays a list of all categories.
     *
     * @param model Spring MVC Model
     * @return The name of the category list view
     */
    @GetMapping
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
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
        model.addAttribute("category", new Category());
        return "category/add";
    }

    /**
     * Handles the submission of a new category.
     *
     * @param category The category to be added
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            Category theCategory = categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category added successfully");
            return "redirect:/categories/" + theCategory.getId();
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/add";
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
            model.addAttribute("category", category);
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
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
            return "redirect:/categories/" + updatedCategory.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/" + id + "/update";
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
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }

}