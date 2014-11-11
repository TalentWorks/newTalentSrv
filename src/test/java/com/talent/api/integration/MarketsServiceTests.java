package com.talent.api.integration;

import org.junit.Test;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class MarketsServiceTests extends TestVerticle {
  Logger log;
  @Override
  public void start() {
    initialize();
    log = container.logger();
    JsonObject config = new JsonObject("{\"dataFile\":\"marketsTestData.json\"}");
    container.deployVerticle("com.godaddy.api.markets.Services.MarketsService", config, 1, deploymentResult -> {
      if (deploymentResult.failed()) {
        container.logger().error(deploymentResult.cause());
      }
      assertTrue(deploymentResult.succeeded());
      assertNotNull("deploymentID should not be null", deploymentResult.result());

      // Wait period to ensure services had time to initialize (not just being deployed)
      vertx.setTimer(500, timerId -> {
        startTests();
      });
    });
  }

  @Test
  public void testQueryByExistingMarketIdReturnsItem() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("id", "es-ar");

    vertx.eventBus().send("marketservice.getItem", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));

          JsonElement results = jsonResponse.body().getElement("results");
          assertNotNull(results);
          assertTrue("Results should be an object", results.isObject());

          JsonObject marketData = results.asObject();
          assertNotNull(marketData);

          assertEquals("es-AR", marketData.getString("id"));
          assertEquals("Argentina", marketData.getString("country"));

          testComplete();
        }
    );
  }

  @Test
  public void testQueryByNonExistingMarketIdReturnsNull() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("id", "foo-bar");

    vertx.eventBus().send("marketservice.getItem", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));
          JsonElement results = jsonResponse.body().getElement("results");
          assertNull(results);

          testComplete();
        }
    );
  }

  @Test
  public void testQueryByNullMarketIdReturnsCollection() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("id", null);

    vertx.eventBus().send("marketservice.getCollection", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));
          JsonElement results = jsonResponse.body().getElement("results");
          assertNotNull(results);
          assertTrue(results.isArray());

          JsonArray marketData = results.asArray();
          assertNotNull(marketData);

          assertTrue(marketData.size() > 0);

          testComplete();
        }
    );
  }

  @Test
  public void testQueryByExistingCountryReturnsCollection() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("country", "Argentina");

    vertx.eventBus().send("marketservice.getCollection", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));
          JsonElement results = jsonResponse.body().getElement("results");
          assertNotNull(results);
          assertTrue(results.isArray());

          JsonArray marketData = results.asArray();
          assertNotNull(marketData);

          assertTrue(marketData.size() > 0);

          testComplete();
        }
    );
  }

  @Test
  public void testQueryByNonExistingCountryReturnsNull() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("country", "Argentina");

    vertx.eventBus().send("marketservice.getCollection", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));
          JsonElement results = jsonResponse.body().getElement("results");
          assertNotNull(results);
          assertTrue(results.isArray());

          JsonArray marketData = results.asArray();
          assertNotNull(marketData);

          assertTrue(marketData.size() > 0);

          testComplete();
        }
    );
  }

  @Test
  public void testQueryByMixedCaseCountryReturnsCollection() {
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.putString("country", "ArGeNtInA");

    vertx.eventBus().send("marketservice.getCollection", jsonRequest, (Message<JsonObject> jsonResponse) -> {
          assertNull("Error not null: " + jsonResponse.body().getString("error"), jsonResponse.body().getString("error"));
          JsonElement results = jsonResponse.body().getElement("results");
          assertNotNull(results);
          assertTrue(results.isArray());

          JsonArray marketData = results.asArray();
          assertNotNull(marketData);

          assertTrue(marketData.size() > 0);

          testComplete();
        }
    );
  }
}
