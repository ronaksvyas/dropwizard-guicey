package ru.vyarus.dropwizard.guice.diagnostic

import io.dropwizard.Application
import ru.vyarus.dropwizard.guice.bundle.GuiceyBundleLookup
import ru.vyarus.dropwizard.guice.diagnostic.support.AutoScanAppWithLookup
import ru.vyarus.dropwizard.guice.diagnostic.support.bundle.*
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooInstaller
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooModule
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooResource
import ru.vyarus.dropwizard.guice.module.GuiceBootstrapModule
import ru.vyarus.dropwizard.guice.module.GuiceyConfigurationInfo
import ru.vyarus.dropwizard.guice.module.context.ConfigScope
import ru.vyarus.dropwizard.guice.module.context.info.InstallerItemInfo
import ru.vyarus.dropwizard.guice.module.installer.CoreInstallersBundle
import ru.vyarus.dropwizard.guice.module.installer.WebInstallersBundle
import ru.vyarus.dropwizard.guice.module.installer.feature.LifeCycleInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.TaskInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.eager.EagerSingletonInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyFeatureInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider.JerseyProviderInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.plugin.PluginInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.WebFilterInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.WebServletInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.listener.WebListenerInstaller
import ru.vyarus.dropwizard.guice.module.installer.scanner.ClasspathScanner
import ru.vyarus.dropwizard.guice.module.jersey.debug.HK2DebugBundle
import ru.vyarus.dropwizard.guice.module.jersey.debug.service.HK2DebugFeature
import ru.vyarus.dropwizard.guice.support.util.GuiceRestrictedConfigBundle
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 26.06.2016
 */
@UseGuiceyApp(AutoScanAppWithLookup)
class AutoScanModeWithLookupDiagnosticTest extends BaseDiagnosticTest {

    @Inject
    GuiceyConfigurationInfo info

    def "Check diagnostic info correctness for enabled lookup and extra bundle"() {

        expect: "correct bundles info"
        // assigned in abstract test
        info.bundles as Set == [HK2DebugBundle, GuiceRestrictedConfigBundle, FooBundle, FooBundleRelativeBundle, CoreInstallersBundle, WebInstallersBundle] as Set
        info.bundlesFromLookup as Set == [HK2DebugBundle, GuiceRestrictedConfigBundle] as Set

        and: "correct installers info"
        // feature installer was installed transitively by Hk2DebugBundle
        def classes = [FooInstaller, FooBundleInstaller, JerseyFeatureInstaller,
                       JerseyFeatureInstaller,
                       JerseyProviderInstaller,
                       ResourceInstaller,
                       EagerSingletonInstaller,
                       HealthCheckInstaller,
                       TaskInstaller,
                       PluginInstaller,
                       WebServletInstaller,
                       WebFilterInstaller,
                       WebListenerInstaller]
        info.installers as Set == classes as Set
        info.installersDisabled as Set == [LifeCycleInstaller, ManagedInstaller] as Set
        info.installersFromScan == [FooInstaller]

        and: "correct extensions info"
        info.extensions as Set == [FooResource, FooBundleResource, HK2DebugFeature] as Set
        info.extensionsFromScan == [FooResource]
        info.getExtensions(ResourceInstaller) as Set == [FooResource, FooBundleResource] as Set
        info.getExtensions(JerseyFeatureInstaller) == [HK2DebugFeature]

        and: "correct modules"
        info.modules as Set == [FooModule, FooBundleModule, GuiceBootstrapModule, HK2DebugBundle.HK2DebugModule, GuiceRestrictedConfigBundle.GRestrictModule] as Set

        and: "correct scopes"
        info.getActiveScopes() == [Application, ClasspathScanner, CoreInstallersBundle, WebInstallersBundle, FooBundle, GuiceRestrictedConfigBundle, HK2DebugBundle, GuiceyBundleLookup] as Set
        info.getItemsByScope(ConfigScope.Application) as Set == [CoreInstallersBundle, FooBundle, FooModule, GuiceBootstrapModule] as Set
        info.getItemsByScope(ConfigScope.ClasspathScan) as Set == [FooInstaller, FooResource] as Set
        info.getItemsByScope(FooBundle) as Set == [FooBundleInstaller, FooBundleResource, FooBundleModule, FooBundleRelativeBundle] as Set
        info.getItemsByScope(ConfigScope.BundleLookup) as Set == [GuiceRestrictedConfigBundle, HK2DebugBundle] as Set
        info.getItemsByScope(GuiceRestrictedConfigBundle) as Set == [GuiceRestrictedConfigBundle.GRestrictModule] as Set
        info.getItemsByScope(HK2DebugBundle) as Set == [JerseyFeatureInstaller, HK2DebugFeature, HK2DebugBundle.HK2DebugModule] as Set

        and: "lifecycle installer was disabled"
        !info.getItemsByScope(CoreInstallersBundle).contains(LifeCycleInstaller)
        InstallerItemInfo li = info.data.getInfo(LifeCycleInstaller)
        !li.enabled
        li.disabledBy == [Application] as Set
        li.registered

        and: "managed installer was disabled"
        !info.getItemsByScope(CoreInstallersBundle).contains(ManagedInstaller)
        InstallerItemInfo mi = info.data.getInfo(ManagedInstaller)
        !mi.enabled
        mi.disabledBy == [FooBundle] as Set
        mi.registered

        and: "feature installer was registered multiple times"
        InstallerItemInfo ji = info.data.getInfo(JerseyFeatureInstaller)
        ji.registeredBy == [CoreInstallersBundle, HK2DebugBundle] as Set
        ji.registrationScopes == [CoreInstallersBundle]
        ji.registrationScopeType == ConfigScope.GuiceyBundle
        ji.registrationAttempts == 2
    }
}
