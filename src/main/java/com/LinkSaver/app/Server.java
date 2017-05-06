package com.LinkSaver.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.eventbus.EventBus;

public class Server extends AbstractVerticle {
	private static final String[] PERMITTED_ADDRESSES = { "create-user" };

	@Override
	public void start(Future<Void> fut) {
		int port = config().getInteger("http.port");

		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route("/eventbus/*").handler(eventBusHandler());

		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));

		router.route().failureHandler(ErrorHandler.create());

		httpServer.requestHandler(router::accept);

		httpServer.listen(port, result -> {
			if (result.succeeded()) {
				EventBus eb = vertx.eventBus();
				
				eb.consumer("create-user", message -> {
					UserService.create().handle(res -> {
						if (res.failed()) {
							message.fail(res.cause().toString());
						} else {
							message.reply(res.result());
						}
					});
				});

				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}

	private SockJSHandler eventBusHandler() {
		PermittedOptions permittedOptions = new PermittedOptions();

		for (String address : PERMITTED_ADDRESSES) {
			permittedOptions.setAddress(address);
		}

		BridgeOptions options = new BridgeOptions().addInboundPermitted(permittedOptions)
				.addInboundPermitted(permittedOptions);

		return SockJSHandler.create(vertx).bridge(options, event -> {
			if (event.type() == BridgeEventType.SOCKET_CREATED) {
				System.out.println("A socket was created");
			}
			event.complete(true);
		});
	}
}