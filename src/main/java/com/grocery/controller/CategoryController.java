package com.grocery.controller;

import com.grocery.dto.CategoryDTO;
import com.grocery.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    // CREATE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {

        logger.info("Creating category: {}", categoryDTO.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.addCategory(categoryDTO));
    }

    // GET CATEGORY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {

        logger.info("Fetching category with id: {}", id);

        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // GET ALL CATEGORIES
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {

        logger.info("Fetching all categories");

        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // UPDATE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {

        logger.info("Updating category with id: {}", id);

        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    // DELETE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        logger.info("Deleting category with id: {}", id);

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }


}