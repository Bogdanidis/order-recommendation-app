package com.example.order_app.service.category;

import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Category;
import com.example.order_app.repository.CategoryRepository;
import com.example.order_app.request.AddCategoryRequest;
import com.example.order_app.request.UpdateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
   private final CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> getAllCategoriesPaginated(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category addCategory(AddCategoryRequest request) {
        return Optional.of(request)
                .filter(c->!categoryRepository.existsByName(c.getName()))
                .map(req -> {
                    Category category = new Category();
                    category.setName(req.getName());
                    category.setDescription(req.getDescription());
                    return categoryRepository.save(category);
                }).orElseThrow(()-> new AlreadyExistsException(request.getName()+" already exists!"));
    }

    @Override
    public Category updateCategory(UpdateCategoryRequest request, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(oldCategory->{
                    oldCategory.setName(request.getName());
                    oldCategory.setDescription(request.getDescription());
                    return categoryRepository.save(oldCategory);
                }).orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }


    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete,()->{
                    throw new ResourceNotFoundException("Category not found!");
                });
    }
}
