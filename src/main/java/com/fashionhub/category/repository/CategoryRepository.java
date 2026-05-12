package com.fashionhub.category.repository;

import com.fashionhub.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by exact name (case-sensitive)
    Optional<Category> findByName(String name);

    // Find all categories whose name contains the keyword (case-insensitive)
    List<Category> findByNameContainingIgnoreCase(String keyword);

    // Check if a category name already exists (for duplicate prevention)
    boolean existsByName(String name);
}