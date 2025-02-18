package ru.vyarus.dropwizard.guice.config.unique

import com.google.inject.Inject
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.AbstractTest
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.diagnostic.support.bundle.Foo2Bundle
import ru.vyarus.dropwizard.guice.diagnostic.support.bundle.FooBundle
import ru.vyarus.dropwizard.guice.module.GuiceyConfigurationInfo
import ru.vyarus.dropwizard.guice.module.context.info.BundleItemInfo
import ru.vyarus.dropwizard.guice.module.context.unique.LegacyModeDuplicatesDetector
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBootstrap
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp

/**
 * @author Vyacheslav Rusakov
 * @since 04.07.2019
 */
@UseGuiceyApp(App)
class LegacyDuplicatesPolicyTest extends AbstractTest {

    @Inject
    GuiceyConfigurationInfo info

    def "Check duplicates allowed and equals handling"() {


        expect: "Foo2 bundle registered just once"
        BundleItemInfo foo2 = info.data.getInfo(Foo2Bundle)
        with(foo2) {
            registrationScopes == [Application]
            registrations == 1

            getRegistrationsByScope(Application).size() == 1
            getDuplicatesByScope(Application).size() == 0

            getRegistrationsByScope(MiddleBundle).size() == 0
            getDuplicatesByScope(MiddleBundle).size() == 1
        }

        and: "Foo registered just once"
        BundleItemInfo foo = info.data.getInfo(FooBundle)
        with(foo) {
            registrationScopes == [Application]
            registrations == 1

            getRegistrationsByScope(Application).size() == 1
            getDuplicatesByScope(Application).size() == 0

            getRegistrationsByScope(MiddleBundle).size() == 0
            getDuplicatesByScope(MiddleBundle).size() == 1
        }
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    // with legacy mode only 1 registration will occur
                    .duplicateConfigDetector(new LegacyModeDuplicatesDetector())
                    .bundles(new FooBundle(), new Foo2Bundle(), new MiddleBundle())
                    .build()
            );
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    static class MiddleBundle implements GuiceyBundle {
        @Override
        void initialize(GuiceyBootstrap bootstrap) {
            // FooBundle should be detected as duplicate due to its equals method
            bootstrap.bundles(new FooBundle(), new Foo2Bundle())
        }
    }
}
