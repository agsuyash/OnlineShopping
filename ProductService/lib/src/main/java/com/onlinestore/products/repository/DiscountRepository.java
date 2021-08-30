package com.onlinestore.products.repository;

import java.util.HashMap;
import java.util.Map;

import com.onlinestore.products.discount.DiscountRequest;
import com.onlinestore.products.discount.DiscountStrategy;

public class DiscountRepository {

	// long productId
	private Map<Long, DiscountStrategy> strategies = new HashMap<Long, DiscountStrategy>();

	// long productId
	private Map<Long, DiscountRequest> requests = new HashMap<Long, DiscountRequest>();

	public void save(DiscountRequest request) {

		if (request != null) {

			long productId = request.getProductId();

			requests.put(productId, request);
		}

	}

	public DiscountRequest getRequest(long product) {
		return requests.get(product);
	}

	public DiscountStrategy getStrategy(long product) {

		if (strategies.containsKey(product)) {
			return strategies.get(product);
		} else {

			DiscountRequest request = requests.get(product);
			if (request != null) {
				long productId = request.getProductId();

				try {

					Class<?> cls = Class.forName(request.getStrategyName());
					DiscountStrategy strategy = (DiscountStrategy) cls.newInstance();
					strategy.setParameters(request.getStrategyParams());
					strategies.put(productId, strategy);
					return strategy;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return null;
	}

	public boolean deleteDiscount(long productid) {

		DiscountRequest request = requests.remove(productid);

		if (request != null && strategies.containsKey(productid)) {
			return strategies.remove(productid) != null;
		}

		return request != null;

	}
}
