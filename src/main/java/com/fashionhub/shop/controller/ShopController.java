package com.fashionhub.shop.controller;

import com.fashionhub.category.entity.Category;
import com.fashionhub.category.service.CategoryService;
import com.fashionhub.product.entity.Product;
import com.fashionhub.product.service.ProductService;
import com.fashionhub.util.ProductImageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ─── GET / → public home page ─────────────────────────────────────
    @GetMapping("/")
    public String index(Model model) {
        // Fetch featured products (limit to 6 for hero section)
        List<Product> allProducts = productService.getAllProducts();
        List<Product> featuredProducts = allProducts.size() > 6 
            ? allProducts.subList(0, 6) 
            : allProducts;

        // Fetch categories
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);

        return "public/home";
    }

    // ─── GET /shop → shop page with all products ──────────────────────
    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        List<Product> products;
        List<Category> categories = categoryService.getAllCategories();

        // Apply filters
        if (search != null && !search.isBlank()) {
            products = productService.searchProducts(search);
        } else if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);

        return "public/shop";
    }

    // ─── GET /product/{id} → product detail page ──────────────────────
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/shop";
        }

        // Fetch related products from same category
        List<Product> relatedProducts = productService.getProductsByCategoryId(product.getCategory().getId());
        relatedProducts.remove(product);
        if (relatedProducts.size() > 4) {
            relatedProducts = relatedProducts.subList(0, 4);
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("defaultImageUrl", ProductImageUtil.FALLBACK_IMAGE);

        return "public/product-detail";
    }
}
