package com.talent.api.Controllers;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;

public class AbstractController implements IController {
  protected final Vertx vertx;
  final EventBus eventBus;

  private AbstractController() {
    vertx = null;
    eventBus = null;
  }

  public AbstractController(Vertx vertx) {
    this.eventBus = vertx.eventBus();
    this.vertx = vertx;
  }

  @Override
  public void handleItem(YokeRequest req) {
    switch  (req.method()) {
      case "DELETE": delete(req);break;
      case "GET": getItem(req);break;
      case "HEAD": req.response().end();break;
      case "POST": notAllowed(req);break;
      case "PUT": checkBodyHeaders(req); put(req);break;
      default: notAllowed(req);
    }
  }

  @Override
  public void handleCollection(YokeRequest req) {
    switch  (req.method()) {
      case "DELETE": notAllowed(req); break;
      case "GET": getCollection(req);break;
      case "HEAD": req.response().end();break;
      case "POST": checkBodyHeaders(req); post(req);break;
      case "PUT": notAllowed(req); break;
      default: notAllowed(req);
    }
  }

  public void getItem(YokeRequest req) {
    notAllowed(req);
  }

  public void getCollection(YokeRequest req) {
    notAllowed(req);
  }

  public void put(YokeRequest req) {
    notAllowed(req);
  }

  public void post(YokeRequest req) {
    notAllowed(req);
  }

  public void delete(YokeRequest req) {
    notAllowed(req);
  }

  public void notAllowed(YokeRequest req) {
    req.response().setStatusCode(405).end();
  }

  private void checkBodyHeaders(YokeRequest req) {
    if (!req.headers().contains("Content-Type")) {
      req.response().setStatusCode(400).setStatusMessage("Content-Type header missing").end();
      return;
    }

    if (!req.headers().get("Content-Type").contains("json")) {
      req.response().setStatusCode(415).setStatusMessage("Unsupported Content-Type: "
          + req.headers().get("Content-Type")
          + ". Verify your request includes the Content-Type header and its value is a valid JSON mime type."
      ).end();
      return;
    }

  }
}
