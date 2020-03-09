package com.payware.bootstrap;

import com.google.inject.AbstractModule;
import com.payware.binding.BusinessModule;
import com.payware.binding.ConfigurationModule;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigurationModule());
        install(new BusinessModule());
        install(new WebModule());
    }
}
