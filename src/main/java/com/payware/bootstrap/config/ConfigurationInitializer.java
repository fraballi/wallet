package com.payware.bootstrap.config;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;

import java.io.File;
import java.util.Collections;

public class ConfigurationInitializer {

    private static final String CONFIG_FILE = "application.yml";

    public ConfigurationProvider configurationProvider() {
        ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(new File(CONFIG_FILE).toPath());
        ConfigurationSource configurationSource = new ClasspathConfigurationSource(configFilesProvider);

        return new ConfigurationProviderBuilder().withConfigurationSource(configurationSource).build();
    }
}
