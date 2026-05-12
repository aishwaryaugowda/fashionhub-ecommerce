package com.fashionhub.category.service;

import com.fashionhub.category.entity.Category;
import java.util.List;

public interface CategoryService {

    // Save a new category
    void saveCategory(Category category);

    // Get all categories
    List<Category> getAllCategories();

    // Get single category by ID
    Category getCategoryById(Long id);

    // Delete category by ID
    void deleteCategoryById(Long id);

    // Check if category name already exists
    boolean isCategoryNameExists(String name);
}