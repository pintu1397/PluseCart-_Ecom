package com.pluse.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pluse.model.Cart;
import com.pluse.model.Category;
import com.pluse.model.OrderRequest;
import com.pluse.model.UserDetail;
import com.pluse.service.CartService;
import com.pluse.service.CategoryService;
import com.pluse.service.ProductOrderService;
import com.pluse.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private ProductOrderService productOrderService;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model model) {
		if (p != null) {
			String email = p.getName();
			UserDetail userDetail = userService.getUserByEmail(email);
			model.addAttribute("user", userDetail);
			Integer countCart = cartService.getCountCart(userDetail.getId());
			model.addAttribute("countCart", countCart);
		

		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		model.addAttribute("categorys", allActiveCategory);
	}
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
	    Cart saveCart = cartService.saveCart(pid, uid);

	    if (ObjectUtils.isEmpty(saveCart)) {
	        session.setAttribute("errorMsg", "Product add to cart failed");
	        System.out.println("Product add to cart failed for pid: " + pid + " and uid: " + uid);
	    } else {
	        session.setAttribute("successMsg", "Product added to cart");
	        System.out.println("Product successfully added to cart for pid: " + pid + " and uid: " + uid);
	    }

	    return "redirect:/product/" + pid;
	}


	

	
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
	    UserDetail user = getLoggedInUserDetails(p);
	    List<Cart> carts = cartService.getCartsByUser(user.getId());
	    m.addAttribute("carts", carts);

	    Double totalOrderPrice = 0.0;
	    for (Cart c : carts) {
	        totalOrderPrice += c.getTotalPrice(); // Use the method to calculate total price
	    }

	    m.addAttribute("totalOrderPrice", totalOrderPrice);
	    return "/user/cart";
	}



	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDetail getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDetail userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
	    UserDetail user = getLoggedInUserDetails(p);
	    List<Cart> carts = cartService.getCartsByUser(user.getId());
	    m.addAttribute("carts", carts);

	    if (!carts.isEmpty()) {
	        // Get total order price by summing up all cart item total prices
	        Double totalOrderPrice = 0.0;
	        for (Cart c : carts) {
	            totalOrderPrice += c.getTotalPrice(); // Add the total price for each cart
	        }

	        // Add additional fees (e.g., shipping, handling)
	        Double orderPrice = totalOrderPrice + 250 + 100;

	        m.addAttribute("orderPrice", orderPrice);
	        m.addAttribute("totalOrderPrice", totalOrderPrice);
	    }
	    return "/user/order";
	}


	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		// System.out.println(request);
		UserDetail user = getLoggedInUserDetails(p);
		productOrderService.saveOrder(user.getId(), request);

		return "redirect:/user/success";
	}
	
	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

}

