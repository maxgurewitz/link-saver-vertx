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
	private static final String[] PERMITTED_ADDRESSES = { "create-user", "test" };

	@Override
	public void start(Future<Void> fut) {
		int port = config().getInteger("http.port");

		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route("/eventbus/*").handler(eventBusHandler());

		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));

		router.route("/*").handler(routingContext -> {
		  HttpServerResponse response = routingContext.response();
		  response.putHeader("content-type", "text/plain");
		  response.end("Hello World from Vert.x-Web!");
		});

		router.route().failureHandler(failureRoutingContext -> {
		  int statusCode = failureRoutingContext.statusCode();
		  
		  if (statusCode >= 400) {
    		  HttpServerResponse response = failureRoutingContext.response();
    		  System.out.println("loc2" + response.getStatusMessage());
    		  System.out.println("loc3" + failureRoutingContext);
    		  System.out.println("loc4" + statusCode);
    		  response.setStatusCode(statusCode).end("Oops, something went wrong");
		  } else {
			  failureRoutingContext.next();
		  }
  	    });

		router.route().failureHandler(ErrorHandler.create());

		httpServer.requestHandler(router::accept);

		httpServer.listen(port, result -> {
			if (result.succeeded()) {
				EventBus eb = vertx.eventBus();
				
				eb.consumer("create-user", message -> {
					System.out.println("create-user: " + message.body());			
				});

				eb.consumer("test").handler(message -> {
//				eb.consumer("test", message -> {
					System.out.println("test: " + message.body());			
				});
				
				eb.consumer("create-user-result", message -> {
					System.out.println("create-user-result: " + message.body());			
				});

				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}

	private SockJSHandler eventBusHandler() {
		PermittedOptions permittedOptions = new PermittedOptions();

//		permittedOptions.setAddressRegex(".*");

		for (String address : PERMITTED_ADDRESSES) {
			System.out.println("loc1" + address);
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