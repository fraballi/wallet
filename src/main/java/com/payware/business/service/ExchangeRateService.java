package com.payware.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payware.bootstrap.config.JavalinConfiguration;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.domain.ExchangeRate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ExchangeRateService {

    private JavalinConfiguration configuration;
    private ExchangeRateRepository exchangeRateRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    public ExchangeRateService(JavalinConfiguration configuration,
        ExchangeRateRepository exchangeRateRepository) {
        this.configuration = configuration;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Transactional
    public ExchangeRate get() throws IOException {

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(configuration.exchangeRateService());
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Accept", "application/json");

            CloseableHttpResponse response = http.execute(get);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Resource unavailable");

            return MAPPER.readValue(EntityUtils.toString(response.getEntity()), ExchangeRate.class);
        }
    }

    public ExchangeRate findBy(long id) {
        return exchangeRateRepository.find(ExchangeRate.class, id);
    }

    public ExchangeRate findByBase(String base){
        return exchangeRateRepository.findByBase(base);
    }

    public List<ExchangeRate> findAll() {
        return new ArrayList<>(exchangeRateRepository.findAll(ExchangeRate.class));
    }
}
