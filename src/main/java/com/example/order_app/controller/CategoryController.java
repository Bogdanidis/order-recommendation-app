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


    @GetMapping
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category/list";
    }


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

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            Category theCategory = categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category added successfully");
            return "redirect:/categories";
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/add";
        }
    }

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

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            redirectAttributes.addFlashAttribute("message", "Category updated successfully");
            return "redirect:/categories";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/" + id + "/update";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }

}