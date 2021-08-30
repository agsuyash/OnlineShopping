package com.onlinestore.products.discount;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;

//"productId" : 1,
//"strategyName": "BuyOneGetOne"
// "params" : {
//	"productId" : 2
//}

public class DiscountRequest {

	private long productId;

	private String strategyName;

	private Map<String, Object> strategyParams = new HashMap<String, Object>();

	public DiscountRequest(long productId, String strategyName, Map<String, Object> strategyParams) {
		super();
		this.productId = productId;
		this.strategyName = strategyName;
		this.strategyParams = strategyParams;
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
	 * @return the strategyName
	 */
	public String getStrategyName() {
		return strategyName;
	}

	/**
	 * @param strategyName
	 *            the strategyName to set
	 */
	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	/**
	 * @return the strategyParams
	 */
	public Map<String, Object> getStrategyParams() {
		return strategyParams;
	}

	/**
	 * @param strategyParams
	 *            the strategyParams to set
	 */
	public void setStrategyParams(Map<String, Object> strategyParams) {
		this.strategyParams = strategyParams;
	}

	public JsonObject toJSon() {
		JsonObject ob = new JsonObject();
		ob.put("productId", getProductId());
		ob.put("strategyName", getStrategyName());

		ob.put("strategyParams", new JsonObject(strategyParams));

		return ob;
	}

	public static DiscountRequest fromJson(JsonObject ob) {
		return new DiscountRequest((long) ob.getInteger("productId"), ob.getString("strategyName"),
				ob.getJsonObject("strategyParams").getMap());
	}

}
