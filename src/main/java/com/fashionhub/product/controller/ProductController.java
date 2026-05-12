package com.fashionhub.product.controller;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.service.CategoryService;
import com.fashionhub.product.entity.Product;
import com.fashionhub.product.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ─── 1. LIST ALL PRODUCTS ─────────────────────────────────────
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products",   productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("product",    new Product());
        return "product/product-list";
    }

    // ─── 2. SAVE NEW PRODUCT ──────────────────────────────────────
    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {

        if (productService.isProductNameExists(product.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Product '" + product.getName() + "' already exists!");
            return "redirect:/products";
        }

        try {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid category selected. Please try again.");
            return "redirect:/products";
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + product.getName() + "' added successfully!");
        return "redirect:/products";
    }

    // ─── 3. DELETE PRODUCT ────────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            Product product = productService.getProductById(id);
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + product.getName() + "' deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products";
    }

    // ════════════════════════════════════════════════════════════════
    // ─── 4. SHOW EDIT FORM ── GET /products/edit/{id} ────────────
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Step 1 — Fetch the existing product by ID
        Product product;
        try {
            product = productService.getProductById(id);
        } catch (RuntimeException e) {
            // If product not found → redirect to list with error
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Product not found with ID: " + id);
            return "redirect:/products";
        }

        // Step 2 — Fetch all categories for the dropdown
        List<Category> categories = categoryService.getAllCategories();

        // Step 3 — Pass both to the view
        model.addAttribute("product",    product);    // pre-fills all form fields
        model.addAttribute("categories", categories); // populates the category dropdown

        // Step 4 — Return the edit view
        return "product/product-edit";
    }

    // ════════════════════════════════════════════════════════════════
    // ─── 5. PROCESS EDIT FORM ── POST /products/update ───────────
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/update")
    public String updateProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {

        // Step 1 — Validate product ID exists in DB (prevent ghost updates)
        try {
            productService.getProductById(product.getId());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Update failed. Product not found with ID: " + product.getId());
            return "redirect:/products";
        }

        // Step 2 — Resolve the selected category from submitted categoryId
        try {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Update failed. Invalid category selected.");
            return "redirect:/products";
        }

        // Step 3 — Save (JPA detects existing ID → runs UPDATE, not INSERT)
        productService.saveProduct(product);

        // Step 4 — Redirect with success message
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + product.getName() + "' updated successfully!");
        return "redirect:/products";
    }
}