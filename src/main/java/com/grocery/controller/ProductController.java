package com.grocery.controller;

import com.grocery.dto.CategoryDTO;
import com.grocery.dto.ProductDTO;
import com.grocery.model.Product;
import com.grocery.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    //CREATE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {

        logger.info("Adding new product: {}", productDTO.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(productDTO));
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {

        logger.info("Fetching product with id: {}", id);

        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET ALL PRODUCTS (PAGINATION)
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching products page: {}, size: {}", page, size);

        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    // UPDATE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {

        logger.info("Updating product with id: {}", id);

        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    // DELETE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        logger.info("Deleting product with id: {}", id);

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String categoryName) {

        logger.info("Fetching products for category: {}", categoryName);

        return ResponseEntity.ok(productService.getProductsByCategory(categoryName));
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }
}