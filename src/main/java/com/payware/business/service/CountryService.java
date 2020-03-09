package com.payware.business.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.payware.bootstrap.config.JavalinConfiguration;
import com.payware.business.repository.CountryRepository;
import com.payware.domain.Country;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Singleton
public class CountryService {

    private JavalinConfiguration configuration;
    private CountryRepository countryRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    public CountryService(JavalinConfiguration configuration,
        CountryRepository countryRepository) {
        this.configuration = configuration;
        this.countryRepository = countryRepository;
    }

    public List<Country> get() throws IOException {

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(configuration.countriesService());
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Accept", "application/json");

            CloseableHttpResponse response = http.execute(get);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Resource unavailable");

            SimpleModule countryModule = new SimpleModule();
            countryModule.addDeserializer(Country.class, new CountryDeserializer());
            MAPPER.registerModule(countryModule);

            List entries = MAPPER
                .readValue(EntityUtils.toString(response.getEntity()), List.class);

            return (List<Country>) entries.stream().map(c -> {
                if (c instanceof Map) {
                    if (((Map) c).containsKey("name")) {
                        return Country.builder().name(String.valueOf(((Map) c).get("name")))
                            .build();
                    }
                }
                return null;
            }).collect(Collectors.toList());
        }
    }

    public Country findBy(long id) {
        return countryRepository.find(Country.class, id);
    }

    public List<Country> findAll() {
        return new ArrayList<>(countryRepository.findAll(Country.class));
    }

    private class CountryDeserializer extends StdDeserializer<Country> {

        public CountryDeserializer() {
            this(null);
        }

        protected CountryDeserializer(Class vc) {
            super(vc);
        }

        @Override
        public Country deserialize(JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException {
            Map<String, Object> node = (Map<String, Object>) jsonParser.getCodec()
                .readValue(jsonParser, Map.class);
            return Country.builder().name(node.get("name").toString()).build();
        }
    }
}
