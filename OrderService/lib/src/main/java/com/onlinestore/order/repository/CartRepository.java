package com.onlinestore.order.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onlinestore.order.datamodel.CartItem;

public class CartRepository {

	// long productId
	private Map<Long, CartItem> items = new HashMap<Long, CartItem>();

	public void save(CartItem item) {
		items.put(item.getProductId(), item);
	}

	public CartItem getCartItem(long productid) {
		return items.get(productid);
	}

	public boolean deleteCartItem(long productid) {
		// TODO Auto-generated method stub
		return items.remove(productid) != null;
	}

	public List<CartItem> getAllCartItems() {
		// TODO Auto-generated method stub
		return new ArrayList<CartItem>(items.values());
	}

}
