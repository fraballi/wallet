package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.payware.business.controller.TransferController;
import io.javalin.Javalin;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransferRouting implements Routing {

    private Javalin app;
    private TransferController controller;

    @Inject
    public TransferRouting(Javalin app, TransferController controller) {
        this.app = app;
        this.controller = controller;
    }

    @Override
    public void accept() {
        app.routes(() -> {
            path("transfers", () -> get(ctx -> controller.findAll(ctx)));
            path("transfers/:id", () -> get(ctx -> controller.findBy(ctx)));
        });
    }
}
