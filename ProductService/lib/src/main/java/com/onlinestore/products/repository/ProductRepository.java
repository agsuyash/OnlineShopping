package com.onlinestore.products.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onlinestore.products.datamodel.Product;

public class ProductRepository {

	//productId
	private Map<Long, Product> items = new HashMap<Long, Product>();

	public ProductRepository() {

	}

	public List<Product> getAllProducts() {
		return new ArrayList<Product>(items.values());
	}

	public void save(Product product) {
		items.put(product.getId(), product);

	}

	public boolean deleteProduct(long productid) {
		return items.remove(productid) != null;
	}

	public Product getProduct(long productid) {
		return items.get(productid);
	}
}
