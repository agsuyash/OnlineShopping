package com.onlinestore.products;

import java.util.HashMap;
import java.util.Map;
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

import com.onlinestore.products.datamodel.Product;
import com.onlinestore.products.discount.DiscountRequest;

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
public class ProductServiceTest {
	static Vertx verx;

	static int port = 3001;

	public ProductServiceTest() {

	}

	@BeforeAll
	@DisplayName("Start Product Service")
	public static void start_server() {
		VertxTestContext testContext = new VertxTestContext();
		verx = Vertx.vertx();
		verx.deployVerticle(new ProductService(port), handler -> {
			if (handler.succeeded()) {
				testContext.completeNow();
			} else {
				testContext.failNow(new RuntimeException("Failed to start Product server"));
			}
		});
		try {
			testContext.awaitCompletion(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Assertions.fail("Failed to start Product Server");
		}
		if (testContext.failed()) {
			Assertions.fail("Failed to start Product Server");
		}

	}

	@Test
	@Order(1)
	public void testAddProduct() {

		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient client = WebClient.create(verx);
		
		Product p = new Product(1, "LG TV", "LG TV 55 inch ", 1299.22);

		client.put(port, "localhost", "/api/products/add").sendJsonObject(p.toJSon(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				// Logger.getGlobal().log(Level.WARNING, "Got HTTP response with
				// status " + response.statusCode());
				// Logger.getGlobal().log
				System.out.println("Got HTTP response with status " + response.statusCode());
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
			Assertions.fail("FAILED to Add product");
		}
	}

	@Test
	@Order(1)
	public void testAddProduct2() {

		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient client = WebClient.create(verx);
		Product p = new Product(2, "Mouse", "Dell Mouse ", 30);

		client.put(port, "localhost", "/api/products/add").sendJsonObject(p.toJSon(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				// Logger.getGlobal().log(Level.WARNING, "Got HTTP response with
				// status " + response.statusCode());
				// Logger.getGlobal().log
				System.out.println("Got HTTP response with status " + response.statusCode());
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
			Assertions.fail("FAILED to Add product");
		}
	}

	@Test
	@Order(2)
	public void testGetProduct() {
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient webClient = WebClient.create(verx);

		webClient.get(port, "localhost", "/api/products/1").send(handler -> {
			if (!handler.succeeded()) {
				future.completeExceptionally(new Throwable("Failed to get products"));
			} else {
				JsonObject ob = handler.result().bodyAsJsonObject();
				future.complete(ob);
			}
		});
		try {
			JsonObject ob = (JsonObject) future.get(1, TimeUnit.SECONDS);
			Assertions.assertNotNull(ob);
			Assertions.assertEquals(ob.getLong("id"), 1);
			Assertions.assertEquals(1299.22, ob.getDouble("price"));
		} catch (Exception e) {
			Assertions.fail("FAILED to get products");
		}
	}

	@Test
	@Order(3)
	public void testGetAllProducts() {
		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient webClient = WebClient.create(verx);
		webClient.get(port, "localhost", "/api/products").send(handler -> {
			if (!handler.succeeded()) {
				future.completeExceptionally(new Throwable("Failed to get products"));
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
			Assertions.assertEquals(ret.getLong("id"), 1);
		} catch (Exception e) {
			Assertions.fail("FAILED to get products");
		}
	}

	@Test
	@Order(4)
	public void testAddDiscount() {

		CompletableFuture<Object> future = new CompletableFuture<Object>();
		WebClient client = WebClient.create(verx);
		Product p = new Product(1, "Mouse", "Dell Mouse ", 30);

		Map<String, Object> strategyParams = new HashMap<>();
		strategyParams.put("productId", 1);
		strategyParams.put("freeProductId", 2);

		DiscountRequest req = new DiscountRequest(1,
				"com.onlinestore.products.discount.BuyOneGetOneFreeDiscountStrategy", strategyParams);

		client.put(port, "localhost", "/api/discount/add").sendJsonObject(req.toJSon(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				System.out.println("Got HTTP response with status " + response.statusCode());
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
			Assertions.fail("FAILED to Add product");
		}
	}

	// @Test
	// @Order(4)
	// public void testDeleteProduct() {
	// CompletableFuture<Object> future = new CompletableFuture<Object>();
	// WebClient webClient = WebClient.create(verx);
	// webClient.delete(port, "localhost",
	// "/api/products/1").send(handler -> {
	// if (!handler.succeeded()) {
	// future.completeExceptionally(new Throwable("Failed to get products"));
	// } else {
	//
	// future.complete(handler.result().statusCode());
	// }
	// });
	// try {
	// Object ob = future.get(1, TimeUnit.SECONDS);
	// Assertions.assertEquals(ob, 200);
	//
	// } catch (Exception e) {
	// Assertions.fail("FAILED to Delete products");
	// }
	//
	// }

}
