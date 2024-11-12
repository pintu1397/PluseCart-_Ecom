package com.pluse.controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pluse.model.Category;
import com.pluse.model.Product;
import com.pluse.model.UserDetail;
import com.pluse.service.CartService;
import com.pluse.service.CategoryService;
import com.pluse.service.ProductService;
import com.pluse.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserService userService;
	@Autowired
	private CartService cartService;
	
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
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
		
	}
	

	@GetMapping("/products")
	public String products(Model model,  @RequestParam(value = "category", defaultValue = "") String category) {
		List<Category> activeCategory = categoryService.getAllActiveCategory();
		List<Product> activeProduct = productService.getAllActiveProducts(category);
		model.addAttribute("categories", activeCategory);
		model.addAttribute("products", activeProduct);
		model.addAttribute("paramValue", category);
		return "product";
	}
	
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "viewProduct";
	}
	
	



	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDetail user, @RequestParam("img") MultipartFile file, HttpSession session)
	        throws IOException {

	    String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
	    user.setProfileImage(imageName);
	    UserDetail saveUser = userService.saveUser(user);

	    if (!ObjectUtils.isEmpty(saveUser)) {
	        if (!file.isEmpty()) {
	            File saveFile = new ClassPathResource("static/images").getFile();

	            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
	                    + file.getOriginalFilename());

	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	        }
	        session.setAttribute("successMsg", "Register successfully");
	        return "redirect:/signin";  // Redirect to signin after successful registration
	    } else {
	        session.setAttribute("errorMsg", "Something went wrong on the server");
	        return "redirect:/register";  // Stay on register page if there is an error
	    }
	}


	
}
