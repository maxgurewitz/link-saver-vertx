package com.LinkSaver.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

public class Server extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {
		int port = config().getInteger("http.port");

		HttpServer httpServer = vertx.createHttpServer();

		httpServer.listen(port, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}
}