package com.fashionhub.product.repository;

import com.fashionhub.product.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category ID
    List<Product> findByCategoryId(Long categoryId);

    // Search by product name
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Filter by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Check duplicate product name
    boolean existsByName(String name);
}