package com.grocery.service;

import com.grocery.dto.ProductDTO;
import com.grocery.exception.ProductException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    Page<ProductDTO> getAllProducts(int page, int size);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<ProductDTO> getProductsByCategory(String category) throws ProductException;
    Page<ProductDTO> searchProducts(String keyword, int page, int size);

}

