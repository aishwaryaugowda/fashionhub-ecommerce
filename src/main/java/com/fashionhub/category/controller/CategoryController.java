package com.fashionhub.category.controller;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Show all categories
    @GetMapping
    public String listCategories(Model model) {

        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());

        return "category/category-list";
    }

    // Save category
    @PostMapping("/save")
    public String saveCategory(
            @ModelAttribute("category") Category category,
            RedirectAttributes redirectAttributes) {

        if (categoryService.isCategoryNameExists(category.getName())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Category already exists!"
            );

            return "redirect:/categories";
        }

        categoryService.saveCategory(category);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Category added successfully!"
        );

        return "redirect:/categories";
    }

    // Delete category
    @GetMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        categoryService.deleteCategoryById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Category deleted successfully!"
        );

        return "redirect:/categories";
    }
}