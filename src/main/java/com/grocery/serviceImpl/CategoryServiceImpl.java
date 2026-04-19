package com.grocery.serviceImpl;

import com.grocery.dto.CategoryDTO;
import com.grocery.exception.CategoryException;
import com.grocery.model.Category;
import com.grocery.repository.CategoryRepository;
import com.grocery.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public CategoryDTO addCategory(CategoryDTO dto) {

        validateCategory(dto);

        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new CategoryException("Category already exists: " + dto.getName());
        }

        Category category = mapper.map(dto, Category.class);

        Category saved = categoryRepository.save(category);

        logger.info("Category created with ID: {}", saved.getId());

        return mapper.map(saved, CategoryDTO.class);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return mapper.map(getCategoryEntity(id), CategoryDTO.class);
    }

    @Cacheable(value = "categories")
    @Override
    public List<CategoryDTO> getAllCategories() {

        logger.info("Fetching all categories from DB");

        return categoryRepository.findAll().stream()
                .map(cat -> mapper.map(cat, CategoryDTO.class))
                .toList();
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {

        Category category = getCategoryEntity(id);

        validateCategory(dto);

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        Category updated = categoryRepository.save(category);

        logger.info("Category updated with ID: {}", id);

        return mapper.map(updated, CategoryDTO.class);
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public void deleteCategory(Long id) {

        Category category = getCategoryEntity(id);

        categoryRepository.delete(category);

        logger.info("Category deleted with ID: {}", id);
    }

    private Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found with ID: " + id));
    }

    private void validateCategory(CategoryDTO dto) {

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new CategoryException("Category name cannot be empty");
        }
    }
}