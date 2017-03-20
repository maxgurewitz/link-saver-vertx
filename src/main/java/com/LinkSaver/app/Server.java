package com.LinkSaver.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class Server extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) {
    String portEnv = System.getenv("PORT");
    Integer port = portEnv == null ? 8080 : Integer.parseInt(portEnv);
    
    vertx
        .createHttpServer()
        .requestHandler(r -> {
          r.response().end("<h1>Hello from my first " +
              "Vert.x 3 application</h1>");
        })
        .listen(port, result -> {
          if (result.succeeded()) {
            System.out.println("server started on port " + port);
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        });
  }
}