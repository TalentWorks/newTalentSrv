package com.talent.api;

import com.jetdrone.vertx.yoke.middleware.AuthHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

public class BasicAuthenticationHandler implements AuthHandler {

  public void handle(String email, String password, Handler next) {
    JsonObject user = new JsonObject();
    if ("feder".equals(email) && "hello".equals(password)) {
      user.putString("email", email);
      user.putString("password", password);
    }

    if (next != null) {
      next.handle(user);
    }
  }
}
