package com.talent.api.Controllers;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

public class UsersController extends AbstractController {

  public UsersController(Vertx vertx) {
    super(vertx);
  }

  @Override
  public void getItem(YokeRequest request) {
    JsonObject jsonMsg = new JsonObject();
    jsonMsg.putString("id", request.params().get("id"));

    eventBus.send("UsersService.GetItem", jsonMsg, (Message<JsonObject> jsonResponse) -> {
          JsonElement results = jsonResponse.body().getElement("result");
          if (results == null) {
            request.response().setStatusCode(404).end();
          } else {
            request.response().headers().set("Content-Type", "application/json");
            String response = results.asObject().encode();
            request.response().end(response);
          }
        }
    );
  }

  public void getCollection(YokeRequest request) {
    JsonObject jsonMsg = new JsonObject();
    jsonMsg
        .putString("action", "find")
        .putString("collection", "User");

    String filter = request.params().get("query");
    if (filter == null) {
      filter = "{}";
    }
    jsonMsg.putObject("filter", new JsonObject(filter));

    String include = request.params().get("keys");
    if (include == null) {
      include = "{}";
    }
    jsonMsg.putObject("include", new JsonObject(include));

    eventBus.send("UsersService.GetCollection", jsonMsg, (Message<JsonObject> jsonResponse) -> {
          JsonElement results = jsonResponse.body().getElement("results");
          request.response().headers().set("Content-Type", "application/json");
          String response = results.asArray().encode();
          request.response().end(response);
        }
    );
  }

  @Override
  public void post(YokeRequest request) {
    JsonObject jsonMsg = new JsonObject();
    JsonObject document = request.body();

    JsonObject validateReq = new JsonObject();
    validateReq
        .putString("action", "validate")
        .putString("key", "UserSchema")
        .putObject("json", document);

    eventBus.send("campudus.jsonvalidator", validateReq, (Message<JsonObject> validateResp) -> {
      if (!"ok".equalsIgnoreCase(validateResp.body().getString("status"))) {
        JsonArray report = validateResp.body().getArray("report");
        request.response().headers().set("Content-Type", "application/json");
        request.response().setStatusCode(400).end(report);
      } else {
        jsonMsg.putObject("document", document);
        eventBus.send("UsersService.CreateItem", jsonMsg, (Message<JsonObject> jsonResp) -> {
              if (!"ok".equalsIgnoreCase(jsonResp.body().getString("status"))) {
                request.response().headers().set("Content-Type", "application/json");
                request.response().setStatusCode(400).end(jsonResp.body());
              } else {
                request.response().headers().set("Content-Type", "application/json");
                JsonElement results = jsonResp.body().getElement("results");
                String response = results.asObject().encode();
                request.response().end(response);
              }
            }
        );
      }
    });
  }
}
