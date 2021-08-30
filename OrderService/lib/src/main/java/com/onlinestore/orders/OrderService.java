package com.onlinestore.orders;

import java.util.List;
import java.util.stream.Collectors;

import com.onlinestore.order.datamodel.CartItem;
import com.onlinestore.order.repository.CartRepository;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class OrderService extends AbstractVerticle {

	private int port;
	private CartRepository repo;

	public OrderService(int port) {
		this.port = port;
	}

	public void start(Promise<Void> startPromise) throws Exception {

		Router router = Router.router(vertx);

		vertx.createHttpServer().requestHandler(router).listen(port, handler -> {
			if (handler.succeeded()) {
				System.out.println("Order Server started.");
				startPromise.complete();

			} else {
				startPromise.fail("Unable to create HTTP Server at port " + port);
			}

		});

		init(router);

	}

	protected void init(Router router) {

		repo = new CartRepository();

		router.put("/api/order/add").handler(requestctx -> {
			requestctx.request().bodyHandler(h -> {
				JsonObject jsonObject = h.toJsonObject();
				if (jsonObject != null) {
					if (addCartItem(jsonObject)) {
						requestctx.response().setStatusCode(200).end();
					} else {
						requestctx.response().setStatusCode(400).end();
					}
				} else {
					requestctx.response().setStatusCode(400).end();
				}
			});

		});

		router.get("/api/order").handler((requestctx) -> {
			getAllCartItems(requestctx);
		});

		router.delete("/api/order/:id").handler(requestctx -> {
			if (deleteCartItem(requestctx))
				requestctx.response().setStatusCode(200).end();
			else
				requestctx.response().setStatusMessage("Could not delete the cart item").setStatusCode(400).end();
		});

		router.route("/api/order/:id").handler(requestctx -> {
			getCartItem(requestctx);
		});

		router.get("/api/total").handler(requestctx -> {
			getTotalAmount(requestctx).onComplete(handler -> {
				if (handler.succeeded()) {
					Double totalcost = (Double) handler.result().body();
					requestctx.response().end("total cost is " + totalcost);
				} else {
					requestctx.response().end("failed to get totalcost");
				}
			});
		});
	}

	private void getCartItem(RoutingContext requestctx) {
		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			CartItem item = repo.getCartItem(productid);
			if (item != null) {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(item.toJSon()));
			} else {
				requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
						.end("Item with product id  " + id + " is not available");
			}

		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}

	}

	private boolean deleteCartItem(RoutingContext requestctx) {
		String id = requestctx.request().getParam("id");
		try {
			long productid = Integer.parseInt(id);
			return repo.deleteCartItem(productid);
		} catch (NumberFormatException e) {
			requestctx.response().setStatusCode(400).end();
		}
		return false;
	}

	private void getAllCartItems(RoutingContext requestctx) {

		List<JsonObject> items = repo.getAllCartItems().stream().map(item -> item.toJSon())
				.collect(Collectors.toList());

		requestctx.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(items));

	}

	private boolean addCartItem(JsonObject ob) {

		// vertx.eventBus().request("api/order", new
		// JsonObject().put("productid", "1"))
		// .onComplete(handler -> {
		// Object result = handler.result();
		// });

		if (ob != null) {
			CartItem item = CartItem.fromJson(ob);
			if (item != null) {
				repo.save(item);
				return true;
			}

		}
		return false;
	}

	private Future<Message<Object>> getTotalAmount(RoutingContext requestctx) {

		List<CartItem> items = repo.getAllCartItems();

		List<JsonObject> lis = items.stream().map(item -> item.toJSon()).collect(Collectors.toList());
		JsonArray array = new JsonArray(lis);
		return vertx.eventBus().request("totalprice", array);

	}

	private double getPrice(long productId) {
		final EventBus eventBus = vertx.eventBus();
		eventBus.request("productprice", productId, handler -> {
			if (handler.succeeded()) {
				Double price = (Double) handler.result().body();
			}
		});
		return 0.0;
	}

}
