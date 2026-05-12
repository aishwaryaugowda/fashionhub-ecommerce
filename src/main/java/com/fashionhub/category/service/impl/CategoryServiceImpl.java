package com.fashionhub.category.service.impl;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.repository.CategoryRepository;
import com.fashionhub.category.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Save Category
    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    // Get All Categories
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get Category By ID
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    // Delete Category
    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Check Duplicate Name
    @Override
    public boolean isCategoryNameExists(String name) {
        return categoryRepository.existsByName(name);
    }
}