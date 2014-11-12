package com.talent.api.Services;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class UsersService extends Verticle {
  Logger log;

  public void start() {
    log = container.logger();

    EventBus eventBus = vertx.eventBus();

    eventBus.registerHandler("UsersService.GetItem",
        (Message<JsonObject> jsonMsg) -> {
          JsonObject body = jsonMsg.body();
          JsonObject mongoReq = new JsonObject();
          mongoReq
              .putString("action", "findone")
              .putString("collection", "User")
              .putObject("matcher", new JsonObject("{\"_id\":\"" + body.getString("id") + "\"}"));

          eventBus.send("vertx.mongopersistor", mongoReq, (Message<JsonObject> mongoResp) -> {
                JsonObject resp = mongoResp.body();

                body.putString("status", resp.getString("status"));

                if (!"ok".equalsIgnoreCase(resp.getString("status"))) {
                  body.putString("message", resp.getString("message"));
                } else {
                  body.putObject("result", resp.getObject("result"));
                }
                jsonMsg.reply(body);
              }
          );
        }
    );

    eventBus.registerHandler("UsersService.GetCollection",
        (Message<JsonObject> jsonMsg) -> {
          JsonObject body = jsonMsg.body();

          JsonObject mongoReq = new JsonObject();
          mongoReq
              .putString("action", "find")
              .putString("collection", "User");

          eventBus.send("vertx.mongopersistor", mongoReq, (Message<JsonObject> mongoResp) -> {
                JsonObject resp = mongoResp.body();

                body.putString("status", resp.getString("status"));

                if (!"ok".equalsIgnoreCase(resp.getString("status"))) {
                  body.putString("message", resp.getString("message"));
                } else {
                  body.putArray("results", resp.getArray("results"));
                }
                jsonMsg.reply(body);
              }
          );
        }
    );

    eventBus.registerHandler("UsersService.CreateItem",
        (Message<JsonObject> jsonMsg) -> {
          JsonObject body = jsonMsg.body();

          JsonObject mongoReq = new JsonObject();
          mongoReq
              .putString("action", "save")
              .putString("collection", "User")
              .putObject("document", body.getObject("document"));

          eventBus.send("vertx.mongopersistor", mongoReq, (Message<JsonObject> mongoResp) -> {
                JsonObject resp = mongoResp.body();

                body.putString("status", resp.getString("status"));

                if (!"ok".equalsIgnoreCase(resp.getString("status"))) {
                  body.putString("message", resp.getString("message"));
                } else {
                  body.putObject("results", new JsonObject().putString("_id", resp.getString("_id")));
                }
                jsonMsg.reply(body);
              }
          );
        }
    );
  }
}
