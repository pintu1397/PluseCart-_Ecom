package com.pluse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private UserDetail user;

    @ManyToOne
    private Product product;

    private Integer quantity;

    // Calculated fields, no need to persist in DB
    private Double totalPrice;
    
    private Double totalOrderPrice;

    // Method to calculate totalPrice for a cart item (product price * quantity)
    public Double getTotalPrice() {
        if (this.product != null && this.quantity != null) {
            return this.product.getDiscountPrice() * this.quantity;
        }
        return 0.0;
    }


	

	public Double getTotalOrderPrice() {
		return totalOrderPrice;
	}

	public void setTotalOrderPrice(Double totalOrderPrice2) {
		this.totalOrderPrice = totalOrderPrice;
		
	}
//	public void setTotalOrderPrice(Double totalOrderPrice) {
//		this.totalOrderPrice = totalOrderPrice;
//	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UserDetail getUser() {
		return user;
	}

	public void setUser(UserDetail user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}


	
	

}
