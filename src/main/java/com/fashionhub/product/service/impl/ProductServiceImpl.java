package com.fashionhub.product.service.impl;

import com.fashionhub.product.entity.Product;
import com.fashionhub.product.repository.ProductRepository;
import com.fashionhub.product.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Save Product
    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // Get All Products
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(this::ensureProductImage);
        return products;
    }

    // Get Product By ID
    @Override
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        ensureProductImage(product);
        return product;
    }

    // Delete Product
    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    // Get Products By Category ID
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(this::ensureProductImage);
        return products;
    }

    // Search Products
    @Override
    public List<Product> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        products.forEach(this::ensureProductImage);
        return products;
    }

    // Filter By Price Range
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        List<Product> products = productRepository.findByPriceBetween(min, max);
        products.forEach(this::ensureProductImage);
        return products;
    }

    // Check Duplicate Product Name
    @Override
    public boolean isProductNameExists(String name) {
        return productRepository.existsByName(name);
    }

    // ─── Combined Search + Filter ────────────────────────────────────
    // Uses Java stream predicates so every param is independently optional.
    // Pass null (or blank string) to skip that filter entirely.
    @Override
    public List<Product> searchAndFilter(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice) {

        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Product> products = productRepository.findAll().stream()
                // keyword filter — case-insensitive name contains
                .filter(p -> kw == null || p.getName().toLowerCase().contains(kw))
                // category filter
                .filter(p -> categoryId == null
                        || (p.getCategory() != null && categoryId.equals(p.getCategory().getId())))
                // min price filter
                .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
                // max price filter
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .collect(java.util.stream.Collectors.toList());

        products.forEach(this::ensureProductImage);
        return products;
    }

    /**
     * Internal logic to automatically fix missing or incorrect image URLs for old records.
     * This keeps the database clean and synced with ProductImageUtil logic.
     */
    private void ensureProductImage(Product product) {
        String url = product.getImageUrl();
        // If image is missing, empty, or an old internet URL (starts with http), 
        // OR it's just the default placeholder, let's try to assign a better themed local image.
        if (url == null || url.isBlank() || url.startsWith("http") || url.equals("/images/default.jpg")) {
            String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "";
            String newUrl = com.fashionhub.util.ProductImageUtil.getImageUrl(categoryName, product.getName());
            
            // Only update if it's actually different or if it was null/empty
            if (url == null || !url.equals(newUrl)) {
                product.setImageUrl(newUrl);
                productRepository.save(product);
            }
        }
    }
}