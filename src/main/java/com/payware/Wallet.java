package com.payware;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.payware.bootstrap.AppModule;
import com.payware.bootstrap.Bootstrap;
import com.payware.bootstrap.data.ContextInitializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Wallet {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        injector.getInstance(Bootstrap.class).start(injector.getInstance(ContextInitializer.class));
    }
}
