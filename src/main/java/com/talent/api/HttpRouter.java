package com.talent.api;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.extras.middleware.Cors;
import com.jetdrone.vertx.yoke.middleware.*;
import com.talent.api.Controllers.IController;
import com.talent.api.Controllers.UsersController;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HttpRouter extends Verticle {
  public void start() {

    Set<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "DELETE", "PUT", "POST"));
    Set<String> allowedHeaders = new HashSet<>(Arrays.asList("Authorization"));

    Yoke yoke = new Yoke(vertx)
        .use(new ErrorHandler(false))                     // Handle errors. See http://pmlopes.github.io/yoke/ErrorHandler.html
        .use(new Limit(4096))                             // Limit size of request body
        .use(new Cors(null, allowedMethods, allowedHeaders, null, false))     // Enable CORS for all origins
        .use(new Logger())                                // Log requests
        .use(new Static("public", 180, false, false))
        .use(new BodyParser());                           // Parse request body

    IController controller = new UsersController(vertx);

    yoke.use(new Router()
            .all("/api/users", controller::handleCollection)
            .all("/api/users/:id", controller::handleItem)
    );

    yoke.listen(getPort(), getHost());
  }

  private String getHost() {
    JsonObject config = container.config();
    String host = config.getString("host");

    if (host == null) {
      return "localhost";
    }

    return host;
  }

  private int getPort() {
    JsonObject config = container.config();
    Number numPort = config.getNumber("port");
    if (numPort == null || numPort.intValue() < 1) {
      return 8080;
    }

    return numPort.intValue();
  }
}
