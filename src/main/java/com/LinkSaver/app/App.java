package com.LinkSaver.app;

import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class App {
	private static final int DEFAULT_PORT = 8080;

	public static void main(String[] args) {
		JsonObject config = new JsonObject();

		String portEnv = System.getenv("PORT");

		int port = portEnv != null ? Integer.parseInt(portEnv) : DEFAULT_PORT;

		config.put("http.port", port);

		DeploymentOptions options = new DeploymentOptions().setConfig(config);

		Vertx.vertx().deployVerticle(Server.class.getName(), options, e -> {
			if (e.failed()) {
				System.out.println(e.cause());
			} else {
				System.out.println("server started on port " + Integer.toString(port));
				System.out.println(e.result());
			}
		});
	}
}
