package com.pluse.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pluse.model.Cart;
import com.pluse.model.Product;
import com.pluse.model.UserDetail;
import com.pluse.repository.CartRepository;
import com.pluse.repository.ProductRepository;
import com.pluse.repository.UserRepository;
import com.pluse.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;




	public Cart saveCart(Integer productId, Integer userId) {

	    // Fetch user and product details
	    UserDetail userDetail = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	    Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

	    // Check if the cart already exists for the user and product
	    Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
	    Cart cart;

	    if (cartStatus == null) {
	        // Cart doesn't exist, create a new cart
	        cart = new Cart();
	        cart.setProduct(product);
	        cart.setUser(userDetail);
	        cart.setQuantity(1); // You can replace this with a dynamic quantity if needed
	        cart.setTotalPrice(1 * product.getDiscountPrice()); // Calculate total price for 1 item
	    } else {
	        // Cart exists, update the quantity and total price
	        cart = cartStatus;
	        cart.setQuantity(cart.getQuantity() + 1); // Increment quantity
	        cart.setTotalPrice(cart.getQuantity() * product.getDiscountPrice()); // Recalculate total price
	    }

	    // Save the cart and return the saved instance
	    return cartRepository.save(cart);
	}


	@Override
	public List<Cart> getCartsByUser(Integer userId) {
	    List<Cart> carts = cartRepository.findByUserId(userId);

	    for (Cart c : carts) {
	        Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
	        c.setTotalPrice(totalPrice); // Set the total price for each cart
	    }

	    return carts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {

		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;

		if (sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}

		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}

}
