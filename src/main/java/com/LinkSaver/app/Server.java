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
import io.vertx.core.eventbus.EventBus;

public class Server extends AbstractVerticle {
	private static final String[] PERMITTED_ADDRESSES = { "create-user" };

	@Override
	public void start(Future<Void> fut) {
	    Settings settings = Settings.singleton;

		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route("/eventbus/*").handler(eventBusHandler());

		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));

		router.route().failureHandler(ErrorHandler.create());

		httpServer.requestHandler(router::accept);

		httpServer.listen(settings.port, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});

        EventBus eb = vertx.eventBus();
        
        eb.consumer("create-user", message -> {
            UserService.create().setHandler(res -> {
                if (res.failed()) {
                    message.fail(0, res.cause().toString());
                } else {
                    message.reply(res.result().toString());
                }
            });
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