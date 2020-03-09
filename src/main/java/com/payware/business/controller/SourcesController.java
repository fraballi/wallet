package com.payware.business.controller;

import com.payware.business.service.CountryService;
import com.payware.business.service.ExchangeRateService;
import com.payware.domain.Country;
import com.payware.domain.ExchangeRate;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.io.IOException;
import java.util.Objects;
import javax.inject.Inject;

public class SourcesController {

    private final CountryService countryService;
    private final ExchangeRateService exchangeService;

    @Inject
    public SourcesController(CountryService countryService, ExchangeRateService exchangeService) {
        this.countryService = countryService;
        this.exchangeService = exchangeService;
    }

    public void countries(Context ctx) throws IOException {
        ctx.json(countryService.get());
    }

    public void exchangeRates(Context ctx) throws IOException {
        ctx.json(exchangeService.get());
    }

    public void findCountryBy(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        Country entity = countryService.findBy(id);
        if (Objects.nonNull(entity)) {
            ctx.json(entity);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void findExchangeRateBy(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        ExchangeRate entity = exchangeService.findBy(id);
        if (Objects.nonNull(entity)) {
            ctx.json(entity);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void findAllCountries(Context ctx) {
        ctx.json(countryService.findAll());
    }

    public void findAllExchangeRates(Context ctx) {
        ctx.json(exchangeService.findAll());
    }
}
