package ru.vyarus.dropwizard.guice.config.support

import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.config.support.conf.ConfigLevel2

/**
 * @author Vyacheslav Rusakov
 * @since 20.06.2016
 */
class NoIfaceBindingApp extends Application<ConfigLevel2> {

    @Override
    void initialize(Bootstrap<ConfigLevel2> bootstrap) {
        bootstrap.addBundle(GuiceBundle.builder()
                .bindConfigurationInterfaces(false)
                .build())
    }

    @Override
    void run(ConfigLevel2 configuration, Environment environment) throws Exception {
    }
}
