package com.pluse.service;

import com.pluse.PluseCartApplication;
import com.pluse.model.*;
import com.pluse.repository.CartRepository;
import com.pluse.repository.ProductOrderRepository;
import com.pluse.service.impl.ProductOrderServiceImpl;
import com.pluse.util.OrderStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PluseCartApplication.class)
class ProductOrderServiceImplTest {

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private ProductOrderServiceImpl productOrderService;

    private OrderRequest orderRequest;
    private List<Cart> cartList;
    private ProductOrder productOrder;

    @BeforeEach
    void setUp() {
        // Sample OrderRequest for testing
        orderRequest = new OrderRequest();
        orderRequest.setFirstName("John");
        orderRequest.setLastName("Doe");
        orderRequest.setEmail("john@example.com");
        orderRequest.setMobileNo("1234567890");
        orderRequest.setAddress("123 Main St");
        orderRequest.setCity("City");
        orderRequest.setState("State");
        orderRequest.setPincode("123456");
        orderRequest.setPaymentType("Credit Card");

        // Sample Cart for testing
        Product product = new Product();
        product.setDiscountPrice(100.0);

        UserDetail user = new UserDetail();
        user.setId(1);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(2);

        cartList = new ArrayList<>();
        cartList.add(cart);

        // Sample ProductOrder for testing
        productOrder = new ProductOrder();
        productOrder.setOrderId(UUID.randomUUID().toString());
        productOrder.setOrderDate(LocalDate.now());
        productOrder.setProduct(product);
        productOrder.setPrice(100.0);
        productOrder.setQuantity(2);
        productOrder.setUser(user);
        productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
    }

    @Test
    void testSaveOrder() {
        // Mock the cart retrieval for the user
        when(cartRepository.findByUserId(1)).thenReturn(cartList);

        // Act: Call saveOrder
        productOrderService.saveOrder(1, orderRequest);

        // Verify that save is called for each order generated from the cart
        verify(productOrderRepository, times(cartList.size())).save(any(ProductOrder.class));
    }

    @Test
    void testGetOrdersByUser() {
        // Mock the orders retrieval by user ID
        List<ProductOrder> orders = new ArrayList<>();
        orders.add(productOrder);
        when(productOrderRepository.findByUserId(1)).thenReturn(orders);

        // Act: Retrieve orders by user ID
        List<ProductOrder> result = productOrderService.getOrdersByUser(1);

        // Assert: Verify the result contains the expected orders
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productOrder, result.get(0));
    }

    @Test
    void testUpdateOrderStatus() {
        // Mock finding the order by ID
        when(productOrderRepository.findById(1)).thenReturn(Optional.of(productOrder));

        // Act: Update the status of the order
        Boolean result = productOrderService.updateOrderStatus(1, OrderStatus.DELIVERED.getName());

        // Assert: Verify the update was successful
        assertTrue(result);
        assertEquals(OrderStatus.DELIVERED.getName(), productOrder.getStatus());

        // Verify that the updated order was saved
        verify(productOrderRepository).save(productOrder);
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        // Mock finding the order by ID (not found)
        when(productOrderRepository.findById(1)).thenReturn(Optional.empty());

        // Act: Attempt to update status for a non-existing order
        Boolean result = productOrderService.updateOrderStatus(1, OrderStatus.DELIVERED.getName());

        // Assert: Verify the update failed
        assertFalse(result);

        // Verify save was not called since the order was not found
        verify(productOrderRepository, never()).save(any(ProductOrder.class));
    }
}
