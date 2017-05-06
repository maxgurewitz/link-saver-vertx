package com.LinkSaver.app;

import io.vertx.core.Vertx;

public class App {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		Settings settings = Settings.singleton;

		vertx.deployVerticle(Server.class.getName(), e -> {
			if (e.failed()) {
				System.out.println(e.cause());
			} else {
				System.out.println("server started on port " + Integer.toString(settings.port));
				System.out.println(e.result());
			}
		});
	}
}
