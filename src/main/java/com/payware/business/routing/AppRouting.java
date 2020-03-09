package com.payware.business.routing;

import com.payware.business.controller.AppController;
import io.javalin.Javalin;

import javax.inject.Inject;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class AppRouting implements Routing {

    private Javalin app;
    private AppController controller;

    @Inject
    public AppRouting(Javalin app, AppController controller) {
        this.app = app;
        this.controller = controller;
    }

    @Override
    public void accept() {
        app.routes(() -> path("health", () -> get(ctx -> controller.health(ctx))));
    }
}
