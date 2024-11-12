package com.pluse.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import com.pluse.model.Category;
import com.pluse.model.Product;
import com.pluse.model.UserDetail;
import com.pluse.service.CartService;
import com.pluse.service.CategoryService;
import com.pluse.service.ProductService;
import com.pluse.service.UserService;

import java.util.List;

@ExtendWith(SpringExtension.class)
class HomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private CartService cartService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(get("/signin"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

//    @Test
//    void testRegister() throws Exception {
//        mockMvc.perform(get("/register"))
//               .andExpect(status().isOk())  // Expect HTTP 200 OK
//               .andExpect(view().name("register"));  // Expect the view name to be "register"
//    }


    @Test
    void testProducts() throws Exception {
        when(categoryService.getAllActiveCategory()).thenReturn(List.of(new Category()));
        when(productService.getAllActiveProducts(anyString())).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/products").param("category", ""))
               .andExpect(status().isOk())
               .andExpect(view().name("product"))
               .andExpect(model().attributeExists("categories"))
               .andExpect(model().attributeExists("products"))
               .andExpect(model().attribute("paramValue", ""));
    }

    @Test
    void testProduct() throws Exception {
        when(productService.getProductById(1)).thenReturn(new Product());

        mockMvc.perform(get("/product/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("viewProduct"))
               .andExpect(model().attributeExists("product"));
    }

    @Test
    void testSaveUser() throws Exception {
        UserDetail user = new UserDetail();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Mock the user service to return the user when saveUser is called
        when(userService.saveUser(any(UserDetail.class))).thenReturn(user);

        // Create a mock file for the upload (e.g., a dummy file)
        MockMultipartFile file = new MockMultipartFile("img", "test.jpg", "image/jpeg", "dummy content".getBytes());

        // Perform the POST request with the file as part of the multipart request
        mockMvc.perform(multipart("/saveUser")
                .file(file)  // Add the mock file here
                .param("name", "John Doe")
                .param("email", "john@example.com"))
               .andExpect(status().is3xxRedirection())   // Expecting a redirect response status (3xx)
               .andExpect(view().name("redirect:/signin"));  // Expecting redirection to "/signin"
    }

    @Test
    void testGetUserDetailsWithNoPrincipal() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeDoesNotExist("user"))
               .andExpect(model().attributeDoesNotExist("countCart"));
    }

    @Test
    void testGetUserDetailsWithPrincipal() throws Exception {
        // Mock the Principal and UserService
        UserDetail user = new UserDetail();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(user);
        when(cartService.getCountCart(user.getId())).thenReturn(5);

        mockMvc.perform(get("/").principal(() -> "john@example.com"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("user", user))
               .andExpect(model().attribute("countCart", 5));
    }
}
