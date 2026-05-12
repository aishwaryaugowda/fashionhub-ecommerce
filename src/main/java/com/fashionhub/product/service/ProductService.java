package com.fashionhub.product.service;

import com.fashionhub.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // Save product
    void saveProduct(Product product);

    // Get all products
    List<Product> getAllProducts();

    // Get product by ID
    Product getProductById(Long id);

    // Delete product
    void deleteProductById(Long id);

    // Get products by category ID
    List<Product> getProductsByCategoryId(Long categoryId);

    // Search products
    List<Product> searchProducts(String keyword);

    // Filter by price range
    List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max);

    // Check duplicate product name
    boolean isProductNameExists(String name);
}