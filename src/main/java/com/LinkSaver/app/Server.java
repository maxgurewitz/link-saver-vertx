package com.LinkSaver.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class Server extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {
		int port = config().getInteger("http.port");

		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route("/eventbus/*").handler(eventBusHandler());
		router.route().failureHandler(ErrorHandler.create());
		router.route().handler(StaticHandler.create().setCachingEnabled(false));

		httpServer.requestHandler(router::accept);

		httpServer.listen(port, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}

	private SockJSHandler eventBusHandler() {
		PermittedOptions permittedOptions = new PermittedOptions().setAddressRegex("login");

		BridgeOptions options = new BridgeOptions().addOutboundPermitted(permittedOptions);

		return SockJSHandler.create(vertx).bridge(options, event -> {
			if (event.type() == BridgeEventType.SOCKET_CREATED) {
				System.out.println("A socket was created");
			}
			event.complete(true);
		});
	}
}