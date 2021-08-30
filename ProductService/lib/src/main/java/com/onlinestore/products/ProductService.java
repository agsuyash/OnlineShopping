package com.onlinestore.products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onlinestore.products.datamodel.Product;
import com.onlinestore.products.discount.DiscountRequest;
import com.onlinestore.products.discount.DiscountStrategy;
import com.onlinestore.products.repository.DiscountRepository;
import com.onlinestore.products.repository.ProductRepository;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProductService extends OnlineStoreService {

	private ProductRepository repo;
	private DiscountRepository discountRepo;

	public ProductService(int port) {
		super(port);
	}

	@Override
	protected void init(Router router) {
		repo = new ProductRepository();
		discountRepo = new DiscountRepository();

		// vertx.eventBus().consumer("productservice/product", handler -> {
		// JsonObject ob = (JsonObject) handler.body();
		//
		// });
		//
		vertx.eventBus().consumer("productprice", handler -> {
			Long productid = (Long) handler.body();
			double price = repo.getProduct(productid).getPrice();
			handler.reply(price);
		});
		vertx.eventBus().consumer("totalprice", handler -> {
			JsonArray cartItems = (JsonArray) handler.body();
			double totalPrice = calculateTotalPrice(cartItems);
			handler.reply(totalPrice);

		});

		router.get("/api/products").handler((requestctx) -> {
			getAllProducts(requestctx);
		});

		router.put("/api/products/add").handler(requestctx -> {
			requestctx.request().bodyHandler(h -> {
				JsonObject jsonObject = h.toJsonObject();
				if (jsonObject != null) {
					if (addProduct(jsonObject)) {
						requestctx.response().setStatusCode(200).end();
					} else {
						requestctx.response().setStatusCode(400).end();
					}
				} else {
					requestctx.response().setStatusCode(400).end();
				}
			});

		});

		router.get("/api/products/:id").handler(requestctx -> {
			getProduct(requestctx);
		});

		router.delete("/api/products/:id").handler(requestctx -> {
			if (deleteProduct(requestctx))
				requestctx.response().setStatusCode(200).end();
			else
				requestctx.response().setStatusMessage("Could not delete the product").setStatusCode(400).end();
		});

		router.put("/api/discount/add").handler(requestctx -> {
			requestctx.request().bodyHandler(h -> {
				JsonObject jsonObject = h.toJsonObject();
				if (jsonObject != null) {
					if (addDiscount(jsonObject)) {
						requestctx.response().setStatusCode(200).end();
					} else {
						requestctx.response().setStatusCode(400).end();
					}
				} else {
					requestctx.response().setStatusCode(400).end();
				}
			});

		});

		router.get("/api/discount/:id").handler(requestctx -> {
			getDiscount(requestctx);
		});

		router.delete("/api/discount/:id").handler(requestctx -> {
			if (deleteDiscount(requestctx))
				requestctx.response().setStatusMessage("Discount deleted").setStatusCode(200).end();
			else
				requestctx.response().setStatusMessage("Could not delete the discount").setStatusCode(400).end();
		});

	}

	private double calculateTotalPrice(JsonArray cartItems) {
		double totalCost = 0.0;

		Map<Product, Integer> items = new HashMap<Product, Integer>();
		List<DiscountStrategy> discounts = new ArrayList<DiscountStrategy>();

		for (Object item : cartItems) {
			JsonObject jsonLineItem = (JsonObject) item;
			long productId = jsonLineItem.getLong("productId");
			int quantity = jsonLineItem.getInteger("quantity");

			Product product = repo.getProduct(productId);
			if (product != null) {
				items.put(product, quantity);
				totalCost += product.getPrice() * quantity;
				DiscountStrategy strategy = discountRepo.getStrategy(productId);
				if (strategy != null) {
					discounts.add(strategy);
				}
			}

		}

		double totalDscount = 0;
		for (DiscountStrategy strategy : discounts) {
			totalDscount += strategy.applyDiscount(items);
		}

		return totalCost - totalDscount;

	}

	private void getProduct(RoutingContext requestctx) {

		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			Product product = repo.getProduct(productid);
			if (product != null) {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(product.toJSon()));
			} else {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end("Product with id  " + id + " is not available");
			}

		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}

	}

	private boolean deleteProduct(RoutingContext requestctx) {
		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			return repo.deleteProduct(productid);
		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}
		return false;

	}

	private void getAllProducts(RoutingContext rc) {
		List<JsonObject> produces = repo.getAllProducts().stream().map(pro -> pro.toJSon())
				.collect(Collectors.toList());
		rc.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(produces));
	}

	private boolean addProduct(JsonObject ob) {

		if (ob != null) {
			Product product = Product.fromJson(ob);
			if (product != null) {
				repo.save(product);
				return true;
			}

		}
		return false;

	}

	private boolean addDiscount(JsonObject ob) {

		if (ob != null) {
			DiscountRequest req = DiscountRequest.fromJson(ob);
			if (req != null) {
				discountRepo.save(req);
				return true;
			}

		}
		return false;
	}

	private void getDiscount(RoutingContext requestctx) {

		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			DiscountRequest product = discountRepo.getRequest(productid);
			if (product != null) {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(product.toJSon()));
			} else {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end("Discount for product id  " + id + " is not available");
			}

		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}

	}

	private boolean deleteDiscount(RoutingContext requestctx) {
		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			return discountRepo.deleteDiscount(productid);
		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}
		return false;

	}

}
