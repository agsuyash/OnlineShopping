package com.onlinestore.products;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public abstract class OnlineStoreService extends AbstractVerticle {
	private int port;

	public OnlineStoreService(int port) {
		this.port = port;
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		Router router = Router.router(vertx);

		vertx.createHttpServer().requestHandler(router).listen(port, handler -> {
			if (handler.succeeded()) {
				System.out.println("Product Server started.");
				startPromise.complete();

			} else {
				startPromise.fail("Unable to create HTTP Server at port " + port);
			}

		});
		
		init(router);

	}

	protected abstract void init(Router router);

}
