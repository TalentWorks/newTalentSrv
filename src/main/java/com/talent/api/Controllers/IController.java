package com.talent.api.Controllers;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;

public interface IController {
  void handleItem(YokeRequest req);
  void handleCollection(YokeRequest req);
}
