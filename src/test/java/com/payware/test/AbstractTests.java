package com.payware.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.payware.bootstrap.AppModule;
import com.payware.bootstrap.Bootstrap;
import com.payware.bootstrap.config.JavalinConfiguration;
import com.payware.bootstrap.data.ContextInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractTests {

  String api;
  private Bootstrap bootstrap;

  @BeforeAll
  protected void init() {
    Injector injector = Guice.createInjector(new AppModule());
    bootstrap = injector.getInstance(Bootstrap.class);
    JavalinConfiguration javalinConfiguration = injector.getInstance(JavalinConfiguration.class);

    api = String.format("%s://%s:%s%s", "http", "localhost", javalinConfiguration.port(),
        javalinConfiguration.contextPath());

    log.debug(api);

    bootstrap.start(injector.getInstance(ContextInitializer.class));
  }

  @AfterAll
  protected void close() {
    bootstrap.stop();
  }
}
