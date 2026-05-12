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
        return productRepository.findAll();
    }

    // Get Product By ID
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with ID: " + id));
    }

    // Delete Product
    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    // Get Products By Category ID
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // Search Products
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Filter By Price Range
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    // Check Duplicate Product Name
    @Override
    public boolean isProductNameExists(String name) {
        return productRepository.existsByName(name);
    }
}