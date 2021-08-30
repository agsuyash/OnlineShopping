package com.onlinestore.products.discount;

import java.util.Map;

import com.onlinestore.products.datamodel.Product;

//100,50, 20, 10
public class BuyOneGetOneFiftyDiscountStrategy extends DiscountStrategy {

	@Override
	// return discounted price
	public double applyDiscount(Map<Product, Integer> items) {

		if (items.size() < 2) {
			return 0;
		}

		// sort by lowest price items
		// Collections.sort(items, new Comparator<CartItem>() {
		// @Override
		// public int compare(CartItem o1, CartItem o2) {
		// return 0;// (int) (o2.getPrice() - o1.getPrice());
		// }
		// });

		// for(int i=0;i< int(item.size()/2); i++){
		//
		// }
		//
		//
		//
		// for (CartItem purchase : item) {
		// total += purchase.getPrice();
		// }

		return 0;
	}

	@Override
	public void setParameters(Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

}
