package com.pluse.service;

import com.pluse.PluseCartApplication;
import com.pluse.model.Product;
import com.pluse.repository.ProductRepository;
import com.pluse.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PluseCartApplication.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MultipartFile image;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setTitle("Sample Product");
        product.setDescription("Sample Description");
        product.setCategory("Electronics");
        product.setPrice(100.0);
        product.setDiscount(10);
        product.setStock(50);
        product.setIsActive(true);
        product.setImage("sample.jpg");
    }

    @Test
    void testSaveProduct() {
        when(productRepository.save(product)).thenReturn(product);
        Product savedProduct = productService.saveProduct(product);
        
        assertNotNull(savedProduct);
        assertEquals(product.getTitle(), savedProduct.getTitle());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Boolean result = productService.deleteProduct(product.getId());

        assertTrue(result);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        Boolean result = productService.deleteProduct(product.getId());

        assertFalse(result);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(product.getId());

        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Mock the existing product
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Mock image upload
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("updated.jpg");
        InputStream inputStream = new FileInputStream("src/main/resources/static/images/ecom.png");
        when(image.getInputStream()).thenReturn(inputStream);

        // Call the update method
        Product updatedProduct = productService.updateProduct(product, image);

        assertNotNull(updatedProduct);
        assertEquals("updated.jpg", updatedProduct.getImage());
        assertEquals(product.getDiscountPrice(), updatedProduct.getDiscountPrice());

        verify(productRepository).save(updatedProduct);
    }

    @Test
    void testUpdateProduct_NoImageUpdate() {
        // Mock the existing product
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Call the update method with empty image
        when(image.isEmpty()).thenReturn(true);
        Product updatedProduct = productService.updateProduct(product, image);

        assertNotNull(updatedProduct);
        assertEquals(product.getImage(), updatedProduct.getImage());
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void testGetAllActiveProducts_NoCategory() {
        List<Product> activeProducts = new ArrayList<>();
        activeProducts.add(product);

        when(productRepository.findByIsActiveTrue()).thenReturn(activeProducts);

        List<Product> result = productService.getAllActiveProducts(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void testGetAllActiveProducts_WithCategory() {
        List<Product> categoryProducts = new ArrayList<>();
        categoryProducts.add(product);

        when(productRepository.findByCategory("Electronics")).thenReturn(categoryProducts);

        List<Product> result = productService.getAllActiveProducts("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }
}
