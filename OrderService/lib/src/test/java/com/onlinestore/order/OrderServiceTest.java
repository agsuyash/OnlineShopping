package com.onlinestore.order;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;

import com.onlinestore.order.datamodel.CartItem;
import com.onlinestore.orders.OrderService;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@Testable
@ExtendWith(VertxExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class OrderServiceTest {
	static Vertx verx;
	
	static int port = 3002;


	public OrderServiceTest() {

	}

	@BeforeAll
	@DisplayName("Start Order Service")
	public static void start_server() {
		VertxTestContext testContext = new VertxTestContext();
		verx = Vertx.vertx();
		verx.deployVerticle(new OrderService(port), handler -> {
			if (handler.succeeded()) {
				testContext.completeNow();
			} else {
				testContext.failNow(new RuntimeException("Failed to start Order server"));
			}
		});
		try {
			testContext.awaitCompletion(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Assertions.fail("Failed to start Order Server");
		}
		if (testContext.failed()) {
			Assertions.fail("Failed to start Order Server");
		}

	}

	@Test
	@Order(1)
	public void testAddCartItem() {

		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient client = WebClient.create(verx);
		CartItem p = new CartItem(1, 2);

		client.put(port, "localhost", "/api/order/add").sendJsonObject(p.toJSon(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				future.complete(response.statusCode());
			} else {
				ar.cause().printStackTrace();
			}
		});
		try {
			Integer code = (Integer) future.get(60, TimeUnit.SECONDS);
			Assertions.assertNotNull(code);
			Assertions.assertEquals(200, code);
		} catch (Exception e) {
			Assertions.fail("FAILED to Add cart item");
		}
	}

	@Test
	@Order(2)
	public void testAddCartItem2() {

		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient client = WebClient.create(verx);
		CartItem p = new CartItem(2, 3);

		client.put(port, "localhost", "/api/order/add").sendJsonObject(p.toJSon(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				future.complete(response.statusCode());
			} else {
				ar.cause().printStackTrace();
			}
		});
		try {
			Integer code = (Integer) future.get(60, TimeUnit.SECONDS);
			Assertions.assertNotNull(code);
			Assertions.assertEquals(200, code);
		} catch (Exception e) {
			Assertions.fail("FAILED to Add order item");
		}
	}

	@Test
	@Order(3)
	public void testGetCartItem() {
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient webClient = WebClient.create(verx);

		webClient.get(port, "localhost", "/api/order/1").send(handler -> {
			if (!handler.succeeded()) {
				future.completeExceptionally(new Throwable("Failed to get order item"));
			} else {
				JsonObject ob = handler.result().bodyAsJsonObject();
				future.complete(ob);
			}
		});
		try {
			JsonObject ob = (JsonObject) future.get(20, TimeUnit.SECONDS);
			Assertions.assertNotNull(ob);
			Assertions.assertEquals(1, ob.getLong("productId"));
			Assertions.assertEquals(2, ob.getInteger("quantity"));
		} catch (Exception e) {
			Assertions.fail("FAILED to get order item");
		}
	}

	@Test
	@Order(4)
	public void testGetAllCartItems() {
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient webClient = WebClient.create(verx);
		webClient.get(port, "localhost", "/api/order").send(handler -> {
			if (!handler.succeeded()) {
				future.completeExceptionally(new Throwable("Failed to get order item"));
			} else {
				JsonArray ob = handler.result().bodyAsJsonArray();
				future.complete(ob);
			}
		});
		try {
			JsonArray ob = (JsonArray) future.get(1, TimeUnit.SECONDS);
			Assertions.assertNotNull(ob);
			Assertions.assertTrue(!ob.isEmpty());
			JsonObject ret = ob.getJsonObject(0);
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(1, ret.getLong("productId"));
		} catch (Exception e) {
			Assertions.fail("FAILED to get order item");
		}
	}
	
	
	@Test
	@Order(5)
	public void testDeleteCartItem() {
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient webClient = WebClient.create(verx);
		webClient.delete(port, "localhost", "/api/order/1").send(handler -> {
			if (!handler.succeeded()) {
				future.completeExceptionally(new Throwable("Failed to delete Cart Item"));
			} else {

				future.complete(handler.result().statusCode());
			}
		});
		try {
			Object ob = future.get(1, TimeUnit.SECONDS);
			Assertions.assertEquals(ob, 200);

		} catch (Exception e) {
			Assertions.fail("FAILED to Delete Cart Item");
		}

	}



}
