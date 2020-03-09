package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.payware.business.controller.CardHolderController;
import io.javalin.Javalin;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CardHolderRouting implements Routing {

    private Javalin app;
    private CardHolderController controller;

    @Inject
    public CardHolderRouting(Javalin app, CardHolderController controller) {
        this.app = app;
        this.controller = controller;
    }

    @Override
    public void accept() {
        app.routes(() -> {
            path("holders", () -> get(ctx -> controller.findAll(ctx)));
            path("holders/:id", () -> get(ctx -> controller.findBy(ctx)));
            path("holders/:id/balance", () -> get(ctx -> controller.balanceById(ctx)));
            path("holders/:id/cards", () -> get(ctx -> controller.cardsById(ctx)));
            path("holders/:id/accounts", () -> get(ctx -> controller.accountsById(ctx)));
            path("holders/transfer", () -> post(ctx -> controller.transfer(ctx)));
        });
    }
}
