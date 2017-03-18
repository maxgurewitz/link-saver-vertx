package com.LinkSaver.app;

import io.vertx.core.Vertx;

public class App
{
	   public static void main(String[] args) {
		    Vertx.vertx().deployVerticle(Server.class.getName(), e -> {
		    	System.out.println("server started");
		    	System.out.println(e.result());
		    });
		    	
	  }
}
