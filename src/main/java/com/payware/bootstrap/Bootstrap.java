package com.payware.bootstrap;

import com.payware.bootstrap.config.JavalinConfiguration;
import com.payware.bootstrap.data.ContextInitializer;
import com.payware.business.routing.Routing;
import io.javalin.Javalin;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

public class Bootstrap {

    @Inject
    private Javalin app;
    @Inject
    private JavalinConfiguration configuration;

    @com.google.inject.Inject(optional = true)
    private Set<Routing> routes = new HashSet<>();

    public void start() {
        app.routes(() -> routes.forEach(Routing::accept)).start(configuration.port());
    }

    public void start(ContextInitializer context) {
        start();
        if (Objects.nonNull(context))
            context.init();
    }

    public void stop() {
        app.stop();
    }
}
