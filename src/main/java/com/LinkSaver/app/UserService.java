package com.LinkSaver.app;

import io.vertx.core.Future;

public class UserService {
	public static Future<User> create() {
		return Future.succeededFuture(new User("maxthegeek1"));
	}
}
