package com.onlinestore.products.discount;

import java.util.Map;
import java.util.Set;

import com.onlinestore.products.datamodel.Product;

public class BuyOneGetOneFreeDiscountStrategy extends DiscountStrategy {

	long productId;

	long freeProductId;

	public BuyOneGetOneFreeDiscountStrategy() {
		super();
	}

	public BuyOneGetOneFreeDiscountStrategy(long productId, long freeProductId) {
		super();
		this.productId = productId;
		this.freeProductId = freeProductId;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.onlinestore.products.discount.DiscountStrategy#applyDiscount(java.util.Map)
	 * @return total discount amount
	 */
	public double applyDiscount(Map<Product, Integer> items) {

		if (items.size() < 2) {
			return 0;
		}

		Product productItem = null;
		Product freeProductItem = null;

		Set<Product> products = items.keySet();

		for (Product item : products) {
			if (item.getId() == productId) {
				productItem = item;
			} else if (item.getId() == freeProductId) {
				freeProductItem = item;
			}
		}

		if (productItem != null && freeProductItem != null) {

			int productQuantity = items.get(productItem);
			int freeProductQuantity = items.get(freeProductItem);

			return Math.min(productQuantity, freeProductQuantity) * freeProductItem.getPrice();

		}

		return 0;
	}

	@Override
	public void setParameters(Map<String, Object> params) {
		if (params != null) {
			productId = (int) params.get("productId");
			freeProductId = (int) params.get("freeProductId");
		}

	}

}
