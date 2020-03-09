package com.payware.test;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.payware.business.repository.CountryRepository;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.domain.Country;
import com.payware.domain.ExchangeRate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class JpaTests {

    private static final List<Country> COUNTRIES = Arrays
        .asList(Country.builder().name("United States of America").build(),
            Country.builder().name("Germany").build());
    private static final List<ExchangeRate> EXCHANGE_RATES = Arrays
        .asList(ExchangeRate.builder().base("USD_").build(),
            ExchangeRate.builder().base("EUR_").build());

    private CountryRepository countryRepository;
    private ExchangeRateRepository exchangeRateRepository;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new TestModule());
        countryRepository = injector.getInstance(CountryRepository.class);
        exchangeRateRepository = injector.getInstance(ExchangeRateRepository.class);

        saveCountries();
        saveExchangeRates();
    }

    @Test
    public void checkCountries() {
        Collection<Country> countries = countryRepository.findAll(Country.class);

        Assert.assertNotNull(countries);
        assertThat(countries.stream().map(Country::getName).collect(toList()))
            .containsAll(COUNTRIES.stream().map(Country::getName).collect(
                toList()));

        log.info("Expected countries: {}", COUNTRIES);
    }

    @Test
    public void checkExchangeRates() {
        ExchangeRate exchangeRates = exchangeRateRepository.findByBase("EUR_");

        Assert.assertNotNull(exchangeRates);
        assertThat(EXCHANGE_RATES.stream().map(ExchangeRate::getBase))
            .contains(exchangeRates.getBase());

        log.info("Expected exchangeRates: {}", EXCHANGE_RATES);
    }

    @Transactional
    private void saveCountries() {
        countryRepository.save(COUNTRIES);
    }

    @Transactional
    private void saveExchangeRates() {
        Collection<ExchangeRate> rates = exchangeRateRepository.findAll(ExchangeRate.class);
        if (!rates.stream().map(ExchangeRate::getBase).collect(toList())
            .containsAll(EXCHANGE_RATES.stream().map(ExchangeRate::getBase).collect(toList())))
            exchangeRateRepository.save(EXCHANGE_RATES);
    }
}
