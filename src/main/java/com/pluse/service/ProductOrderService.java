package com.pluse.service;

import java.util.List;

import com.pluse.model.OrderRequest;
import com.pluse.model.ProductOrder;

public interface ProductOrderService {

    public void saveOrder(Integer userid,OrderRequest orderRequest);
	
	public List<ProductOrder> getOrdersByUser(Integer userId);
	
	public Boolean updateOrderStatus(Integer id,String status);
}
