package com.pluse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pluse.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	public List<Product> findByIsActiveTrue();

	List<Product> findByCategory(String category);

}
