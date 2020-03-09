package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import com.payware.business.controller.BankAccountsController;
import io.javalin.Javalin;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BankAccountRouting implements Routing {

  private Javalin app;
  private BankAccountsController controller;

  @Inject
  public BankAccountRouting(Javalin app, BankAccountsController controller) {
    this.app = app;
    this.controller = controller;
  }

  @Override
  public void accept() {
    app.routes(() -> {
      path("accounts", () -> get(ctx -> controller.findAll(ctx)));
      path("accounts/:id", () -> get(ctx -> controller.findBy(ctx)));
    });
  }
}
