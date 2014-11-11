package com.talent.api;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class AppStart extends Verticle {
  public void start() {
    JsonObject config = container.config();
    if (config == null) {
      config = new JsonObject();
    }

    Integer instancesPerCore = config.getInteger("instancesPerCore");
    if (instancesPerCore == null || instancesPerCore <= 0) {
      instancesPerCore = 1;
    }

    int numInstances = instancesPerCore * Runtime.getRuntime().availableProcessors();

    Logger log = container.logger();
    log.info("AppStart: Starting with config:\n" + config.encodePrettily());
    log.info("AppStart: Number of instances: " + numInstances);

    container.deployVerticle("com.godaddy.api.markets.HttpRouter", config.getObject("router"), numInstances);
    container.deployVerticle("com.godaddy.api.markets.Services.MarketsService", config.getObject("marketService"), numInstances);
  }
}
