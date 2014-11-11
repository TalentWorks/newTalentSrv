package com.talent.api.Controllers;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

public class MarketsController extends AbstractController {

  public MarketsController(Vertx vertx) {
    super(vertx);
  }

  @Override
  public void getItem(YokeRequest request) {
    JsonObject jsonMsg = new JsonObject()
        .putString("plid", request.params().get("plid"))
        .putString("id", request.params().get("id"));

    eventBus.send("marketservice.getItem", jsonMsg, (Message<JsonObject> jsonResponse) -> {
          JsonElement responseElement = jsonResponse.body().getElement("results");
          if (responseElement == null) {
            request.response().setStatusCode(404).end();
          } else {
            request.response().headers().set("Content-Type", "application/json");
            String response = responseElement.asObject().encode();
            request.response().end(response);
          }
        }
    );
  }

  public void getCollection(YokeRequest request) {
    JsonObject jsonMsg = new JsonObject()
        .putString("plid", request.params().get("plid"))
        .putString("country", request.params().get("country"))
        .putString("subdomain", request.params().get("subdomain"));

    eventBus.send("marketservice.getCollection", jsonMsg, (Message<JsonObject> jsonResponse) -> {
          JsonElement responseElement = jsonResponse.body().getElement("results");
          request.response().headers().set("Content-Type", "application/json");
          String response = responseElement.asArray().encode();
          request.response().end(response);
        }
    );
  }
}
