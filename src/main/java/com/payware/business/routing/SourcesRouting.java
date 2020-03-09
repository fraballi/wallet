package com.payware.business.routing;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import com.payware.business.controller.SourcesController;
import io.javalin.Javalin;
import javax.inject.Inject;

public class SourcesRouting implements Routing {

    private Javalin app;
    private SourcesController controller;

    @Inject
    public SourcesRouting(Javalin app, SourcesController controller) {
        this.app = app;
        this.controller = controller;
    }

    @Override
    public void accept() {
        app.routes(() -> {
            path("countries", () -> get(ctx -> controller.findAllCountries(ctx)));
            path("countries/:id", () -> get(ctx -> controller.findCountryBy(ctx)));
            path("rest/countries", () -> get(ctx -> controller.countries(ctx)));
            path("rates", () -> get(ctx -> controller.findAllExchangeRates(ctx)));
            path("rates/:id", () -> get(ctx -> controller.findExchangeRateBy(ctx)));
            path("rest/rates", () -> get(ctx -> controller.exchangeRates(ctx)));
        });
    }
}
