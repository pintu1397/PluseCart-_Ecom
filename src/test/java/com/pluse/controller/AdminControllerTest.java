package com.pluse.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.pluse.PluseCartApplication;
import com.pluse.model.Category;
import com.pluse.model.Product;
import com.pluse.model.UserDetail;
import com.pluse.service.CartService;
import com.pluse.service.CategoryService;
import com.pluse.service.ProductService;
import com.pluse.service.UserService;

@SpringBootTest(classes = PluseCartApplication.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;
    
    @Mock
    private Principal principal;
    
    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testGetUserDetails() {
        // Arrange
        UserDetail mockUser = new UserDetail();
        mockUser.setId(1);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(mockUser);
        when(cartService.getCountCart(mockUser.getId())).thenReturn(3);

        // Act
        adminController.getUserDetails(principal, model);

        // Assert
        verify(model).addAttribute("user", mockUser);
        verify(model).addAttribute("countCart", 3); // Ensure `countCart` is set to 3 as expected
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    void testCategory() throws Exception {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getAllCategory()).thenReturn(categories);

        mockMvc.perform(get("/admin/category"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category"))
                .andExpect(model().attribute("categories", categories));
    }

    @Test
    void testSaveCategory() throws Exception {
        Category category = new Category();
        category.setName("Test Category");
        category.setIsActive(true);

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "dummy content".getBytes());

        when(categoryService.existCategory("Test Category")).thenReturn(false);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(multipart("/admin/saveCategory")
                .file(file)
                .flashAttr("category", category))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        int categoryId = 1;
        when(categoryService.deleteCategory(categoryId)).thenReturn(true);

        mockMvc.perform(get("/admin/deleteCategory/{id}", categoryId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category"));
    }

    @Test
    void testLoadEditCategory() throws Exception {
        int categoryId = 1;
        Category category = new Category();
        category.setId(categoryId);
        
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        mockMvc.perform(get("/admin/loadEditCategory/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/editCategory"))
                .andExpect(model().attribute("category", category));
    }

    @Test
    void testUpdateCategory() throws Exception {
        Category category = new Category();
        category.setId(1);
        category.setName("Updated Category");

        MockMultipartFile file = new MockMultipartFile("file", "updated.jpg", "image/jpeg", "dummy content".getBytes());

        when(categoryService.getCategoryById(1)).thenReturn(category);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(multipart("/admin/updateCategory")
                .file(file)
                .flashAttr("category", category))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/loadEditCategory/" + category.getId()));
    }

    @Test
    void testLoadAddProduct() throws Exception {
        mockMvc.perform(get("/admin/loadAddProduct"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/addProduct"));
    }

    @Test
    void testSaveProduct() throws Exception {
        Product product = new Product();
        product.setTitle("New Product");

        MockMultipartFile image = new MockMultipartFile("file", "product.jpg", "image/jpeg", "dummy content".getBytes());

        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(multipart("/admin/saveProduct")
                .file(image)
                .flashAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/loadAddProduct"));
    }

    @Test
    void testLoadViewProduct() throws Exception {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/products"))
                .andExpect(model().attribute("products", products));
    }

    @Test
    void testDeleteProduct() throws Exception {
        int productId = 1;
        when(productService.deleteProduct(productId)).thenReturn(true);

        mockMvc.perform(get("/admin/deleteProduct/{id}", productId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setTitle("Updated Product");

        MockMultipartFile image = new MockMultipartFile("file", "updated.jpg", "image/jpeg", "dummy content".getBytes());

        when(productService.updateProduct(any(Product.class), any(MultipartFile.class))).thenReturn(product);

        mockMvc.perform(multipart("/admin/updateProduct")
                .file(image)
                .flashAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/editProduct/" + product.getId()));
    }
}
