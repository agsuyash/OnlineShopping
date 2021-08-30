package com.onlinestore.products.discount;

import java.util.Map;

import com.onlinestore.products.datamodel.Product;

public abstract class DiscountStrategy {

	public abstract double applyDiscount(Map<Product , Integer> items);

	public abstract void setParameters(Map<String, Object> params);

}
