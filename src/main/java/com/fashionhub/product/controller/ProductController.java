package com.fashionhub.product.controller;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.service.CategoryService;
import com.fashionhub.product.entity.Product;
import com.fashionhub.product.service.ProductService;
import com.fashionhub.util.ProductImageUtil;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ─── 1. LIST / SEARCH / FILTER PRODUCTS ──────────────────────
    @GetMapping
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            Model model) {

        // Decide whether any filter is active
        boolean hasFilter = (keyword != null && !keyword.isBlank())
                || categoryId != null
                || minPrice != null
                || maxPrice != null;

        List<Product> products = hasFilter
                ? productService.searchAndFilter(keyword, categoryId, minPrice, maxPrice)
                : productService.getAllProducts();

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("product", new Product());

        // Echo filter values back so the form stays filled after submit
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("hasFilter", hasFilter);

        // Add default image for UI fallbacks
        model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);

        return "product/product-list";
    }

    // ─── 2. SAVE NEW PRODUCT ──────────────────────────────────────
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("categoryId") Long categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Custom validation: Duplicate Name Check
        if (product.getName() != null && productService.isProductNameExists(product.getName())) {
            result.rejectValue("name", "error.product", "Product '" + product.getName() + "' already exists!");
        }

        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);
            return "product/product-list";
        }

        try {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);

            // Auto-assign image based on category and name
            product.setImageUrl(ProductImageUtil.getImageUrl(category.getName(), product.getName()));

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Invalid category selected. Please try again.");
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);
            return "product/product-list";
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + product.getName() + "' added successfully!");
        return "redirect:/products";
    }

    // ─── 3. DELETE PRODUCT ────────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable("id") Long id,
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
            @PathVariable("id") Long id,
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
        model.addAttribute("product", product); // pre-fills all form fields
        model.addAttribute("categories", categories); // populates the category dropdown
        model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);

        // Step 4 — Return the edit view
        return "product/product-edit";
    }

    // ─── 5. PROCESS EDIT FORM ── POST /products/update ───────────
    @PostMapping("/update")
    public String updateProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("categoryId") Long categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);
            return "product/product-edit";
        }

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

            // Auto-update image based on (potentially new) category and name
            product.setImageUrl(ProductImageUtil.getImageUrl(category.getName(), product.getName()));

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Update failed. Invalid category selected.");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/product-edit";
        }

        // Step 3 — Save (JPA detects existing ID → runs UPDATE, not INSERT)
        productService.saveProduct(product);

        // Step 4 — Redirect with success message
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + product.getName() + "' updated successfully!");
        return "redirect:/products";
    }
}