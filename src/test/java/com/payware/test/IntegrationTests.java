package com.payware.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
import com.payware.domain.Country;
import com.payware.domain.ExchangeRate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

class IntegrationTests extends AbstractTests {

    @Test
    void checkCountriesApi() {
        HttpResponse<String> response = Unirest.get(api + "/rest/countries").asString();
        assertThat(response.isSuccess()).isTrue();

        Country[] restCountries = response.mapBody(body -> new Gson().fromJson(body, Country[].class));
        assertThat(restCountries.length == 0).isFalse();

        Set<String> names = Stream.of(restCountries).map(Country::getName).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> mockCountries = Arrays.asList("France", "Germany");
        assertThat(names.containsAll(mockCountries)).isTrue();
    }

    @Test
    void checkExchangeRatesApi() {
        HttpResponse<String> response = Unirest.get(api + "/rest/rates").asString();
        assertThat(response.isSuccess()).isTrue();

        ExchangeRate restRate = response.mapBody(body -> new Gson().fromJson(body, ExchangeRate.class));
        assertThat(restRate == null).isFalse();
        assertThat(restRate.getRates().isEmpty()).isFalse();

        Set<String> currencies = restRate.getRates().keySet();
        List<String> mockCurrencies = Arrays.asList("GBP", "USD");
        assertThat(currencies.containsAll(mockCurrencies)).isTrue();
    }
}
