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

    vertx.eventBus().setDefaultReplyTimeout(1000);

    container.deployVerticle("com.talent.api.HttpRouter", config.getObject("router"), numInstances);
    container.deployVerticle("com.talent.api.Services.UsersService", config.getObject("userService"), numInstances);
    container.deployModule("com.campudus~json-schema-validator_2.11~1.2.0", config.getObject("jsonValidator"), deploymentId -> {
      if(deploymentId.failed()) {
        log.error("Json-Validator failed to deploy: " + deploymentId.cause().toString());
      } else {
        log.info("Json-Validator deployed with result: " + deploymentId.result());
      }
    });
    container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", config.getObject("mongodb"), deploymentId -> log.info("Mongo-Persistor deployed with result: " + deploymentId.result()));
  }
}
