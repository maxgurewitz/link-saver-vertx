package com.LinkSaver.app;

import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class App {
	private static final int DEFAULT_PORT = 8080;
	private static final String DEFAULT_ENV = "development";

	public static void main(String[] args) {
		String systemPort = System.getenv("PORT");
		String systemEnv = System.getenv("ENV");

		int port = systemPort != null ? Integer.parseInt(systemPort) : DEFAULT_PORT;
		String env = systemEnv != null ? systemEnv : DEFAULT_ENV;

		JsonObject config = new JsonObject().put("http.port", port).put("env", env);

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
