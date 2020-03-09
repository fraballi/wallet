package com.payware.binding;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.payware.bootstrap.config.ConfigurationInitializer;
import com.payware.bootstrap.config.JavalinConfiguration;
import com.payware.logging.Convertable;
import com.payware.logging.ConvertableLogger;
import com.payware.logging.TransferLogger;
import com.payware.logging.Transferable;
import com.payware.util.HibernateUtil;
import io.javalin.plugin.json.JavalinJackson;
import org.hibernate.SessionFactory;

public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {

        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transferable.class), new TransferLogger());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Convertable.class),
            new ConvertableLogger());

        bind(JavalinConfiguration.class).toInstance(new ConfigurationInitializer().configurationProvider().bind("javalin", JavalinConfiguration.class));

        bind(SessionFactory.class).toInstance(HibernateUtil.getSessionFactory());

        JavalinJackson
            .configure(new ObjectMapper().disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).disable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }
}
