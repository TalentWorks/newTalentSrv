package com.talent.api.Services;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.util.*;
import java.util.stream.Collectors;

public class UsersService extends Verticle {
  Logger log;

  Map<String, Map<String, Object>> marketMap = new TreeMap<>();
  JsonArray marketJsonArray;

  public void start() {
    log = container.logger();

    getMarketsFromFile();

    EventBus eventBus = vertx.eventBus();

    eventBus.registerHandler("marketservice.getItem",
        (Message<JsonObject> jsonMsg) -> {
          JsonObject body = jsonMsg.body();

          if (marketJsonArray == null) {
            body.putString("error", "No market data");
            body.putString("errorLevel", "Fatal");
            jsonMsg.reply(body);
            return;
          }

          String id = body.getString("id");
          String country = body.getString("country");
          String subdomain = body.getString("subdomain");

          body.putObject("results", queryByMarketId(id));

          jsonMsg.reply(body);
        }
    );

    eventBus.registerHandler("marketservice.getCollection",
        (Message<JsonObject> jsonMsg) -> {
          JsonObject body = jsonMsg.body();

          if (marketJsonArray == null) {
            body.putString("error", "No market data");
            body.putString("errorLevel", "Fatal");
            jsonMsg.reply(body);
            return;
          }

          String country = body.getString("country");
          String subdomain = body.getString("subdomain");

          if (country != null) {
            body.putArray("results", queryByCountry(country));
          } else if (subdomain != null) {
            body.putArray("results", queryBySubdomain(subdomain));
          } else {
            body.putArray("results", marketJsonArray);
          }

          jsonMsg.reply(body);
        }
    );
  }

  private void getMarketsFromFile() {
    String dataFile = getMarketDataFileName();

    vertx.fileSystem().readFile(dataFile, ar -> {
      if (ar.succeeded()) {
        parseMarketData(ar.result().toString());
      } else {
        log.error("Failed to read", ar.cause());
      }
    });
  }

  private String getMarketDataFileName() {
    JsonObject config = container.config();

    String dataFile = config.getString("dataFile");
    if (dataFile == null) {
      return "markets.json";
    }

    return dataFile;
  }

  private void parseMarketData(String jsonData) {
    marketJsonArray = new JsonArray(jsonData);

    marketJsonArray.forEach(
        market -> {
          JsonObject marketObject = (JsonObject) market;
          marketMap.put(marketObject.getString("id").toLowerCase(), marketObject.toMap());
        }
    );
  }

  private JsonObject queryByMarketId(String id) {
    if (id == null) return null;

    Map<String, Object> market = marketMap.get(id.toLowerCase());

    if (market == null) {
      return null;
    }

    return new JsonObject(market);
  }

  private JsonArray queryByCountry(String country) {
    return queryByStringProperty("country", country);
  }

  private JsonArray queryBySubdomain(String subdomain) {
    return queryByStringProperty("subdomain", subdomain);
  }

  private JsonArray queryByStringProperty(String propertyName, String propertyValue) {
    if (propertyName == null || propertyValue == null) return null;

    List<Map<String, Object>> list = marketMap.values().stream()
        .filter(market -> propertyValue.equalsIgnoreCase((String) market.get(propertyName)))
        .collect(Collectors.toList());

    return new JsonArray(list);
  }
}
