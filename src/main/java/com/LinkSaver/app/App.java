package com.LinkSaver.app;

import io.vertx.core.Vertx;

public class App
{
	   public static void main(String[] args) {
		    Vertx.vertx().deployVerticle(Server.class.getName(), e -> {	
		    	if (e.failed()) {
			    	System.out.println(e.cause());
		    	} else {
			    	System.out.println(e.result());
		    	}
		    });
		    	
	  }
}
