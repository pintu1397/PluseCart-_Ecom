package com.pluse.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pluse.model.Cart;
import com.pluse.model.Category;
import com.pluse.model.OrderRequest;
import com.pluse.model.UserDetail;
import com.pluse.service.CartService;
import com.pluse.service.CategoryService;
import com.pluse.service.ProductOrderService;
import com.pluse.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(UserController.class)
@MockBean(CategoryService.class)
@MockBean(UserService.class)
@MockBean(CartService.class)
@MockBean(ProductOrderService.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private CartService cartService;
    
    @Mock
    private ProductOrderService productOrderService;

    @InjectMocks
    private UserController userController;

    private UserDetail mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserDetail();
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/user/"))
               .andExpect(status().isOk())
               .andExpect(view().name("user/home"));
    }

    @Test
    void testGetUserDetails() throws Exception {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getAllActiveCategory()).thenReturn(categories);
        when(userService.getUserByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(cartService.getCountCart(mockUser.getId())).thenReturn(3);

        mockMvc.perform(get("/user/"))
               .andExpect(status().isOk())
               .andExpect(view().name("user/home"))
               .andExpect(model().attributeExists("user"))
               .andExpect(model().attribute("countCart", 3))
               .andExpect(model().attribute("categorys", categories));
    }

    @Test
    void testAddToCart() throws Exception {
        Cart mockCart = new Cart();
        when(cartService.saveCart(1, 1)).thenReturn(mockCart);

        mockMvc.perform(get("/user/addCart")
                .param("pid", "1")
                .param("uid", "1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/product/1"));

        verify(cartService, times(1)).saveCart(1, 1);
    }

    @Test
    void testLoadCartPage() throws Exception {
        // Mock user login
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        List<Cart> carts = Arrays.asList(new Cart(), new Cart());
        when(cartService.getCartsByUser(mockUser.getId())).thenReturn(carts);
        when(cartService.getCountCart(mockUser.getId())).thenReturn(3);

        mockMvc.perform(get("/user/cart")
                .session(session)) // Use the session with the logged-in user
                .andExpect(status().isOk())
                .andExpect(view().name("user/cart"))
                .andExpect(model().attributeExists("carts"))
                .andExpect(model().attributeExists("totalOrderPrice"));
    }

    @Test
    void testUpdateCartQuantity() throws Exception {
        mockMvc.perform(get("/user/cartQuantityUpdate")
                .param("sy", "increase")
                .param("cid", "1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/user/cart"));

        verify(cartService, times(1)).updateQuantity("increase", 1);
    }

    @Test
    void testOrderPage() throws Exception {
        List<Cart> carts = Arrays.asList(new Cart(), new Cart());
        when(cartService.getCartsByUser(mockUser.getId())).thenReturn(carts);
        when(cartService.getCountCart(mockUser.getId())).thenReturn(3);

        mockMvc.perform(get("/user/orders"))
               .andExpect(status().isOk())
               .andExpect(view().name("user/order"))
               .andExpect(model().attributeExists("carts"))
               .andExpect(model().attributeExists("orderPrice"))
               .andExpect(model().attributeExists("totalOrderPrice"));
    }

    @Test
    void testSaveOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        mockMvc.perform(post("/user/save-order")
                .param("orderDetails", "some order details"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/user/success"));

        verify(productOrderService, times(1)).saveOrder(mockUser.getId(), orderRequest);
    }

    @Test
    void testLoadSuccess() throws Exception {
        mockMvc.perform(get("/user/success"))
               .andExpect(status().isOk())
               .andExpect(view().name("user/success"));
    }
}
