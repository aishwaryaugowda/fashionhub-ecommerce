package com.fashionhub.dashboard.controller;

import com.fashionhub.category.service.CategoryService;
import com.fashionhub.product.entity.Product;
import com.fashionhub.product.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ─── GET /login → show custom login page ─────────────────────────
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // ─── GET /dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<Product> allProducts = productService.getAllProducts();

        long totalProducts = allProducts.size();
        long totalCategories = categoryService.getAllCategories().size();

        // Low stock = stock quantity <= 5 (matches existing stock-low badge logic)
        long lowStockCount = allProducts.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() <= 5)
                .count();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("lowStockCount", lowStockCount);

        return "dashboard/dashboard";
    }
}
