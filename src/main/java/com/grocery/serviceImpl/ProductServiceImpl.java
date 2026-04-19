package com.grocery.serviceImpl;

import com.grocery.dto.ProductDTO;
import com.grocery.exception.ProductException;
import com.grocery.model.Product;
import com.grocery.repository.ProductRepository;
import com.grocery.repository.UserRepository;
import com.grocery.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;


    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {

        validateProduct(productDTO);

        Product product = mapper.map(productDTO, Product.class);

        Product savedProduct = productRepository.save(product);

        logger.info("Product created with ID: {}", savedProduct.getId());

        return mapper.map(savedProduct, ProductDTO.class);
    }


    private void validateProduct(ProductDTO dto) {

        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new ProductException("Price must be greater than 0");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ProductException("Product name cannot be empty");
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with ID: " + id));
        return mapper.map(product, ProductDTO.class);
    }

    @Cacheable(value = "products", key = "#page + '-' + #size")
    @Override
    public Page<ProductDTO> getAllProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(product -> mapper.map(product, ProductDTO.class));
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto) {

        Product product = getProductEntity(id);

        validateProduct(dto);

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        Product updated = productRepository.save(product);

        logger.info("Product updated with ID: {}", id);

        return mapper.map(updated, ProductDTO.class);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public void deleteProduct(Long id) {

        Product product = getProductEntity(id);

        productRepository.delete(product);

        logger.info("Product deleted with ID: {}", id);
    }

    private Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with ID: " + id));
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) throws ProductException {

        List<Product> products = productRepository.findByCategory_NameIgnoreCase(category);

        if (products.isEmpty()) {
            throw new ProductException("No Products found for the given Category: " + category + " !\nPlease add some products to the category and try again.");
        }

        return products.stream()
                .map(product -> mapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, int page, int size) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ProductException("Search keyword cannot be empty");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchProducts(keyword.trim(), pageable);

        return products.map(product -> mapper.map(product, ProductDTO.class));
    }




}