package com.pluse.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pluse.model.Product;
import com.pluse.repository.ProductRepository;
import com.pluse.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {

	    Product dbProduct = getProductById(product.getId());
	    String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

	    dbProduct.setTitle(product.getTitle());
	    dbProduct.setDescription(product.getDescription());
	    dbProduct.setCategory(product.getCategory());
	    dbProduct.setPrice(product.getPrice());
	    dbProduct.setStock(product.getStock());
	    dbProduct.setImage(imageName);
        dbProduct.setIsActive(product.getIsActive());

	    dbProduct.setDiscount(product.getDiscount());
        dbProduct.setIsActive(product.getIsActive());
	    Double discount = product.getPrice() * (product.getDiscount() / 100.0); // Use 100.0 to ensure double division
	    //System.out.println("discount: " + discount);
	    // System.out.println("price: " + product.getPrice());
	    Double discountPrice = product.getPrice() - discount;
	    //System.out.println("discountPrice: " + discountPrice);
	    dbProduct.setDiscountPrice(discountPrice);

	    Product updateProduct = productRepository.save(dbProduct);

	    if (updateProduct != null && !image.isEmpty()) {
	        try {
	            File saveFile = new ClassPathResource("static/images").getFile();
	            File productImgDir = new File(saveFile.getAbsolutePath() + File.separator + "product_img");
	            
	            // Create directory if it does not exist
	            if (!productImgDir.exists()) {
	                productImgDir.mkdirs();
	            }

	            Path path = Paths.get(productImgDir.getAbsolutePath() + File.separator + image.getOriginalFilename());
	            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return updateProduct;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		}else {
			products=productRepository.findByCategory(category);
		}

		return products;
	}

	
}
