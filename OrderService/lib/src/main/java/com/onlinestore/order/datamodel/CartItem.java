package com.onlinestore.order.datamodel;

import io.vertx.core.json.JsonObject;

public class CartItem {

	private long productId;

	private int quantity;

	public CartItem(long productId, int quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	/**
	 * @return the productId
	 */
	public long getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(long productId) {
		this.productId = productId;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public JsonObject toJSon() {
		JsonObject ob = new JsonObject();
		ob.put("productId", getProductId());
		ob.put("quantity", getQuantity());
		return ob;
	}

	public static CartItem fromJson(JsonObject ob) {

		return new CartItem(ob.getInteger("productId"), ob.getInteger("quantity"));
	}

}
