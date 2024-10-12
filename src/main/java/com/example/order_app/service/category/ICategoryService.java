package com.example.order_app.service.category;

import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import com.example.order_app.request.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Page<Category> getAllCategoriesPaginated(Pageable pageable);

    Category addCategory(AddCategoryRequest request);
    Category updateCategory(UpdateCategoryRequest request, Long id);
    void deleteCategoryById(Long id);

    Page<Category> searchCategories(Pageable pageable, String name);
}
