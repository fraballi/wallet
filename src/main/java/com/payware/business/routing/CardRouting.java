package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.payware.business.controller.CardController;
import io.javalin.Javalin;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CardRouting implements Routing {

  private Javalin app;
  private CardController controller;

  @Inject
  public CardRouting(Javalin app, CardController controller) {
    this.app = app;
    this.controller = controller;
  }

  @Override
  public void accept() {
    app.routes(() -> {
      path("cards", () -> get(ctx -> controller.findAll(ctx)));
      path("cards/:id", () -> get(ctx -> controller.findBy(ctx)));
      path("cards/:id/balance", () -> get(ctx -> controller.balanceById(ctx)));
      path("cards/:id/account", () -> get(ctx -> controller.accountById(ctx)));
      path("cards/:pan/pan", () -> get(ctx -> controller.findByPan(ctx)));
      path("cards/:pan/pan/balance", () -> get(ctx -> controller.balanceByPan(ctx)));
      path("cards/transfer", () -> post(ctx -> controller.transfer(ctx)));
    });
  }
}
