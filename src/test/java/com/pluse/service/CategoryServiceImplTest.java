package com.pluse.service;

import com.pluse.PluseCartApplication;
import com.pluse.model.Category;
import com.pluse.repository.CategoryRepository;
import com.pluse.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PluseCartApplication.class)

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Electronics");
        category.setIsActive(true);
    }

    @Test
    void saveCategory_ShouldReturnSavedCategory() {
        // Arrange
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category savedCategory = categoryService.saveCategory(category);

        // Assert
        assertNotNull(savedCategory);
        assertEquals("Electronics", savedCategory.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void getAllCategory_ShouldReturnAllCategories() {
        // Arrange
        Category category2 = new Category();
        category2.setName("Books");
        List<Category> categoryList = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<Category> result = categoryService.getAllCategory();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void existCategory_ShouldReturnTrueIfCategoryExists() {
        // Arrange
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        // Act
        boolean exists = categoryService.existCategory("Electronics");

        // Assert
        assertTrue(exists);
        verify(categoryRepository).existsByName("Electronics");
    }

    @Test
    void deleteCategory_ShouldReturnTrueIfCategoryDeleted() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Act
        boolean isDeleted = categoryService.deleteCategory(1);

        // Assert
        assertTrue(isDeleted);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_ShouldReturnFalseIfCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        boolean isDeleted = categoryService.deleteCategory(1);

        // Assert
        assertFalse(isDeleted);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryIfFound() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Act
        Category foundCategory = categoryService.getCategoryById(1);

        // Assert
        assertNotNull(foundCategory);
        assertEquals("Electronics", foundCategory.getName());
        verify(categoryRepository).findById(1);
    }

    @Test
    void getCategoryById_ShouldReturnNullIfCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        Category foundCategory = categoryService.getCategoryById(1);

        // Assert
        assertNull(foundCategory);
        verify(categoryRepository).findById(1);
    }

    @Test
    void getAllActiveCategory_ShouldReturnActiveCategoriesOnly() {
        // Arrange
        Category category2 = new Category();
        category2.setName("Furniture");
        category2.setIsActive(false);
        List<Category> activeCategories = Arrays.asList(category);
        when(categoryRepository.findByIsActiveTrue()).thenReturn(activeCategories);

        // Act
        List<Category> result = categoryService.getAllActiveCategory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(categoryRepository).findByIsActiveTrue();
    }
}
