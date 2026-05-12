package com.fashionhub.category.controller;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
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
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Custom duplicate check
        if (category.getName() != null && categoryService.isCategoryNameExists(category.getName())) {
            result.rejectValue("name", "error.category", "Category '" + category.getName() + "' already exists!");
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "category/category-list";
        }

        categoryService.saveCategory(category);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Category added successfully!");

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
                "Category deleted successfully!");

        return "redirect:/categories";
    }
}