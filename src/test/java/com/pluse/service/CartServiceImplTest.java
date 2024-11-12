package com.pluse.service;

import com.pluse.model.Cart;
import com.pluse.model.Product;
import com.pluse.model.UserDetail;
import com.pluse.repository.CartRepository;
import com.pluse.repository.ProductRepository;
import com.pluse.repository.UserRepository;
import com.pluse.service.impl.CartServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class) // Enable Mockito for this test class
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private UserDetail user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new UserDetail();
        user.setId(1);

        product = new Product();
        product.setId(1);
        product.setDiscountPrice(100.0);

        cart = new Cart();
        cart.setId(1);
        cart.setProduct(product);
        cart.setUser(user);
        cart.setQuantity(1);
        cart.setTotalPrice(100.0);
    }

    @Test
    void saveCart_ShouldCreateNewCart() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartRepository.findByProductIdAndUserId(1, 1)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart savedCart = cartService.saveCart(1, 1);

        assertNotNull(savedCart);
        assertEquals(1, savedCart.getQuantity());
        assertEquals(100.0, savedCart.getTotalPrice());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void saveCart_ShouldUpdateExistingCart() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartRepository.findByProductIdAndUserId(1, 1)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart updatedCart = cartService.saveCart(1, 1);

        assertNotNull(updatedCart);
        assertEquals(2, updatedCart.getQuantity());
        assertEquals(200.0, updatedCart.getTotalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void getCartsByUser_ShouldReturnCartsWithCorrectTotalPrice() {
        List<Cart> carts = new ArrayList<>();
        carts.add(cart);

        when(cartRepository.findByUserId(1)).thenReturn(carts);

        List<Cart> userCarts = cartService.getCartsByUser(1);

        assertEquals(1, userCarts.size());
        assertEquals(100.0, userCarts.get(0).getTotalPrice());
    }

    @Test
    void getCountCart_ShouldReturnCartCount() {
        when(cartRepository.countByUserId(1)).thenReturn(1);

        int cartCount = cartService.getCountCart(1);

        assertEquals(1, cartCount);
        verify(cartRepository).countByUserId(1);
    }

    @Test
    void updateQuantity_ShouldIncrementQuantity() {
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));

        cartService.updateQuantity("in", 1);

        assertEquals(2, cart.getQuantity());
        assertEquals(200.0, cart.getTotalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void updateQuantity_ShouldDecrementQuantityAndDeleteIfZero() {
        cart.setQuantity(1);
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));

        cartService.updateQuantity("de", 1);

        verify(cartRepository).delete(cart);
    }
}
