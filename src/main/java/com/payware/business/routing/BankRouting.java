package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import com.payware.business.controller.BankController;
import io.javalin.Javalin;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BankRouting implements Routing {

  private Javalin app;
  private BankController controller;

  @Inject
  public BankRouting(Javalin app, BankController controller) {
    this.app = app;
    this.controller = controller;
  }

  @Override
  public void accept() {
    app.routes(() -> {
      path("banks", () -> get(ctx -> controller.findAll(ctx)));
      path("banks/:id", () -> get(ctx -> controller.findBy(ctx)));
    });
  }
}
