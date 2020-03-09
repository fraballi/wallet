package com.payware.bootstrap;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.payware.binding.ConfigurationModule;
import com.payware.bootstrap.config.JavalinConfiguration;
import io.javalin.Javalin;

import javax.inject.Singleton;

import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class WebModule extends AbstractModule {

    private static final String DEFAULT_CONTEXT_PATH = "/";

    @Override
    protected void configure() {

        bind(Javalin.class).toInstance(Javalin.create((config) -> {

            Injector injector = Guice.createInjector(new ConfigurationModule());
            JavalinConfiguration configuration = injector.getInstance(JavalinConfiguration.class);
            config.contextPath = !isNullOrEmpty(configuration.contextPath()) ? configuration.contextPath().replaceAll("//", "/") : DEFAULT_CONTEXT_PATH;
        }));
    }
}
